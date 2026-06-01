package delivery.redis;

import redis.clients.jedis.Jedis;

public class RedisCourierStatusExample {

    public static void main(String[] args) {

        Jedis jedis = new Jedis("localhost", 6379);

        /*
         * CREATE
         */

        jedis.hset(
                "courier:1",
                "status",
                "AVAILABLE"
        );

        jedis.hset(
                "courier:1",
                "activeOrders",
                "0"
        );

        /*
         * READ
         */

        System.out.println(
                jedis.hgetAll("courier:1")
        );

        /*
         * UPDATE
         */

        jedis.hset(
                "courier:1",
                "status",
                "DELIVERING"
        );

        /*
         * DELETE
         */

        jedis.del("courier:1");

        jedis.close();
    }
}