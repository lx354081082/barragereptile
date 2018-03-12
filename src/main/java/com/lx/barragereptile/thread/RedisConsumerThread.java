package com.lx.barragereptile.thread;

import com.alibaba.fastjson.JSON;
import com.lx.barragereptile.po.RedisBarrage;
import com.lx.barragereptile.po.RedisUser;
import com.lx.barragereptile.pojo.DouyuBarrage;
import com.lx.barragereptile.pojo.PandaBarrage;
import com.lx.barragereptile.service.RedisService;
import com.lx.barragereptile.util.BarrageConstant;
import com.lx.barragereptile.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis任务队列消费线程
 */
@Component
@Scope("prototype")
@Slf4j
public class RedisConsumerThread implements Runnable {
    @Autowired
    RedisService redisService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    //私有全局变量
    private static ArrayList<RedisBarrage> redisBarrages = new ArrayList<>();
    //全局用户信息
    private static Map<String, RedisUser> pandaUser = new HashMap<>();
    private static Map<String, RedisUser> douyuUser = new HashMap<>();

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            RedisBarrage rpop = redisService.rpop(BarrageConstant.BARRAGE);

            //弹幕数据放入数组待用
            redisBarrages.add(rpop);

            //统计弹幕信息
            count(rpop);


            //数量超过400 批量存数据库
            if (redisBarrages.size() >= 2000) {
                try {
                    toDb();
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                }
                redisBarrages.clear();
            }
            //panda用户信息持久化
            if (pandaUser.size() >= 1000) {
                try {
                    toPandaDb();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pandaUser.clear();
            }
            //去','
            if (douyuUser.size() >= 1000) {
                try {
                    toDouyuDb();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                douyuUser.clear();
            }
        }
    }

    private void toDouyuDb() throws Exception {
        StringBuffer sql = new StringBuffer("REPLACE INTO douyu_user (u_id, level, u_name) VALUES ");

        Set<String> strings = douyuUser.keySet();
        for (String key : strings) {
            RedisUser redisUser = douyuUser.get(key);
            if (redisUser.getLevel() == null) {
                continue;
            }
            sql.append("('" + redisUser.getUid() + "'," + redisUser.getLevel() + ",'" + key + "'),");
        }
        //去','
        if (',' == sql.charAt(sql.length() - 1)) {
            sql = sql.deleteCharAt(sql.length() - 1);
            jdbcTemplate.execute(sql.toString());
        }
    }

    /**
     * 用户信息持久化 panda
     */
    private void toPandaDb() {
        StringBuffer sql = new StringBuffer("REPLACE INTO panda_user (u_id, level, u_name) VALUES ");

        Set<String> strings = pandaUser.keySet();
        for (String key : strings) {
            RedisUser redisUser = pandaUser.get(key);
            sql.append("('" + redisUser.getUid() + "'," + redisUser.getLevel() + ",'" + key + "'),");
        }
        if (',' == sql.charAt(sql.length() - 1)) {
            sql = sql.deleteCharAt(sql.length() - 1);
            jdbcTemplate.execute(sql.toString());
        }

    }

    /**
     * 持久化弹幕数据
     */
    private void toDb() throws Exception {
        StringBuffer pandaSql = new StringBuffer("insert into panda_barrage (content, date, level, nickname, rid, roomid) values ");
        StringBuffer douyuSql = new StringBuffer("insert into douyu_barrage (date, level, roomid, txt, uid, uname) values ");

        for (RedisBarrage r : redisBarrages) {
            if (r.getWhere().equals(BarrageConstant.PANDA)) {
                PandaBarrage pandaBarrage = JSON.parseObject(r.getBarrage().toString(), PandaBarrage.class);
                pandaSql.append(pandaSql(pandaBarrage));
            }
            if (r.getWhere().equals(BarrageConstant.DOUYU)) {
                DouyuBarrage douyuBarrage = JSON.parseObject(r.getBarrage().toString(), DouyuBarrage.class);
                douyuSql.append(douyuSql(douyuBarrage));
            }
        }

        //最后一个字符是,就删掉 执行sql
        try {
            if (',' == pandaSql.charAt(pandaSql.length() - 1)) {
                pandaSql = pandaSql.deleteCharAt(pandaSql.length() - 1);
                jdbcTemplate.execute(pandaSql.toString());
            }
            if (',' == douyuSql.charAt(douyuSql.length() - 1)) {
                douyuSql = douyuSql.deleteCharAt(douyuSql.length() - 1);
                jdbcTemplate.execute(douyuSql.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼接 insert sql字符串 数据异常返回 ""
     */
    String pandaSql(PandaBarrage pandaBarrage) {
        StringBuffer sb = new StringBuffer("(");
        sb.append("'" + pandaBarrage.getContent() + "',");
        sb.append("'" + DateFormatUtils.fmtToString(pandaBarrage.getDate(),DateFormatUtils.DATE_TIME_PATTERN) + "',");
        sb.append("" + pandaBarrage.getLevel() + ",");
        sb.append("'" + pandaBarrage.getNickname() + "',");
        sb.append("'" + pandaBarrage.getRid() + "',");
        sb.append("'" + pandaBarrage.getRoomid() + "'),");
        return sb.toString();
    }
    String douyuSql(DouyuBarrage douyuBarrage) {
        try {
            StringBuffer sb = new StringBuffer("(");
            sb.append("'" + DateFormatUtils.fmtToString(douyuBarrage.getDate(), DateFormatUtils.DATE_TIME_PATTERN) + "',");
            sb.append("" + douyuBarrage.getLevel() + ",");
            sb.append("'" + douyuBarrage.getRoomid() + "',");

            sb.append("'" + stringFmt(douyuBarrage.getTxt()) + "',");
            sb.append("'" + douyuBarrage.getUid() + "',");
            sb.append("'" + douyuBarrage.getUname() + "'),");
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    //

    /**
     * 清除特定字符 (')
     */
    private String stringFmt(String s) throws Exception {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length ; i++) {
            if (chars[i] == '\'') {
                chars[i] = ' ';
            }
        }
        String s1 = new String(chars);
        return s1;
    }

    /**
     * 统计用户信息
     */
    private void count(RedisBarrage redisBarrage) {
        if (redisBarrage.getWhere().equals(BarrageConstant.PANDA)) {
            PandaBarrage pandaBarrage = JSON.parseObject(redisBarrage.getBarrage().toString(), PandaBarrage.class);
            //用户统计
            String rid = pandaBarrage.getRid();
            Integer level = pandaBarrage.getLevel();
            pandaUser.put(pandaBarrage.getNickname(), new RedisUser(rid, level));

        }
        if (redisBarrage.getWhere().equals(BarrageConstant.DOUYU)) {
            DouyuBarrage douyuBarrage = JSON.parseObject(redisBarrage.getBarrage().toString(), DouyuBarrage.class);
            //用户统计
            String uid = douyuBarrage.getUid();
            Integer level = douyuBarrage.getLevel();
            douyuUser.put(douyuBarrage.getUname(), new RedisUser(uid, level));
        }
    }

    private void userCount(RedisBarrage redisBarrage) {

    }
//    public static void main(String[] args) {
//        String s = "213'41234";
//        String string = stringFmt(s);
//        System.out.println(string);
//    }
}
