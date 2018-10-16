package com.auto.common.utils.db.cache;


import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

	private static final String NORMAL = "normal";
	private static final String CLUSTER = "cluster";
	private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);
	private static RedisUtils redisCacheCluster = null;
	private static JedisPoolConfig jedisPoolConfig;
	private int permanentTTl = 60 * 60 * 24;

	private RedisUtils() {
	}

	public static void setInstance(JedisPoolConfig jedisPoolConfig) {
		if (redisCacheCluster == null) {
			redisCacheCluster = new RedisUtils();
			redisCacheCluster.jedisPoolConfig = jedisPoolConfig;
			redisCacheCluster.init();
		} else {
		}
	}

	/**
	 * Get Jedis pool client
	 *
	 * @param redisHost
	 * @param redisPort
	 * @return
	 */
	public static JedisPool connectToJedis(String redisHost, int redisPort) {
		try {
			return new JedisPool(jedisPoolConfig,
					redisHost, redisPort);
		} catch (Exception e) {
			throw new RuntimeException("Could not configure Jedis Cluster", e);
		}
	}

	/**
	 * Get Jedis cluster client
	 *
	 * @param redisHost
	 * @param redisPort
	 * @return
	 */
	public static JedisCluster connectToJedisCluster(String redisHost, int redisPort) {
		try {
			Set<HostAndPort> connectionPoints = null;
			connectionPoints = new HashSet<HostAndPort>();
			connectionPoints.add(new HostAndPort(redisHost, redisPort));
			return new JedisCluster(connectionPoints, jedisPoolConfig);
		} catch (Exception e) {
			throw new RuntimeException("Could not configure Jedis Cluster", e);
		}
	}

	/**
	 * Get keys from redis
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param type
	 * @return
	 */
	public static Set getKeys(String redisHost, int redisPort, String type, String key) {
		Set<String> value = null;
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedis = connectToJedis(redisHost, redisPort);
					value = jedis.getResource().keys(key);
					jedis.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					value = jedis.getResource().keys(key);
					jedis.close();
					break;
			}
		} catch (Exception error) {
			logger.error("The exception in getkeys method is : " + error);
		}
		return value;
	}

	/**
	 * Del key from Redis
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param key
	 * @param type
	 */
	public static void delKey(String redisHost, int redisPort, String key, String type) {
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					jedisCluster.del(key);
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					jedis.getResource().del(key);
					jedis.close();
					break;
			}
		} catch (Exception error) {
			logger.error("The exception in delKey method is : " + error);
		}
	}

	/**
	 * Remove value from list
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param key
	 * @param count
	 * @param value
	 * @param type
	 */
	public static void lRemove(String redisHost, int redisPort, String key, long count, String[] value, String type) {
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					for (int i = 0; i < value.length; i++) {
						jedisCluster.lrem(key, count, value[i]);
					}
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					for (int i = 0; i < value.length; i++) {
						jedis.getResource().lrem(key, count, value[i]);
					}
					jedis.close();
					break;
			}
		} catch (Exception error) {
			logger.error("The exception in lRemove method is : " + error);
		}
	}

	/**
	 * Push value into list
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param key
	 * @param value
	 * @param type
	 */
	public static void rPush(String redisHost, int redisPort, String key, String[] value, String type) {
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					jedisCluster.rpush(key, value);
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					jedis.getResource().rpush(key, value);
					jedis.close();
					break;
			}
		} catch (Exception error) {
			logger.error("The exception in rPush method is : " + error);
		}
	}

	/**
	 * Set value
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param key
	 * @param value
	 * @param type
	 */
	public static void updateKey(String redisHost, int redisPort, String key, String[] value, String type) {
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					jedisCluster.set(key, value[0]);
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					jedis.getResource().set(key, value[0]);
					jedis.close();
					break;
			}
		} catch (Exception error) {
			logger.error("The exception in updateKey method is : " + error);
		}
	}

	/**
	 * Get TTL
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param key
	 * @param type
	 * @return
	 */
	public static Long getTtl(String redisHost, int redisPort, String key, String type) {
		try {
			long ttl;
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					ttl = jedisCluster.ttl(key);
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					ttl = jedis.getResource().ttl(key);
					jedis.close();
					break;
			}
			return ttl;
		} catch (Exception error) {
			logger.error("The exception in getTtl method is : " + error);
		}
		return null;
	}

	/**
	 * get Type
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param key
	 * @param type
	 * @return
	 */
	public static String getType(String redisHost, int redisPort, String key, String type) {
		try {
			String keyType;
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					keyType = jedisCluster.type(key);
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					keyType = jedis.getResource().type(key);
					jedis.close();
					break;
			}
			return keyType;
		} catch (Exception error) {
			logger.error("The exception in getType method is : " + error);
		}
		return null;
	}

	/**
	 * Get values
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param key
	 * @param type
	 * @param field
	 * @return
	 */
	public static Object getValues(String redisHost, int redisPort, String key, String type, String field) {
		Object value = null;
		try {
			JSONObject obj = null;
			String keyType;
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					if (jedisCluster.exists(key).booleanValue() == true) {
						keyType = jedisCluster.type(key);
						switch (keyType) {
							case "string":
								value = jedisCluster.get(key);
								break;
							case "list":
								value = jedisCluster.lrange(key, 0, -1);
								break;
							case "hash":
								if (field == null) {
									obj = new JSONObject(jedisCluster.hgetAll(key));
									value = obj;
								} else {
									try {
										value = jedisCluster.hget(key, field);
									} catch (Exception e) {
										value = "";
									}
								}
								break;
							case "set":
								value = jedisCluster.smembers(key);
								break;
							case "zset":
								value = jedisCluster.zrange(key, 0, -1);
								break;
							case "none":
								value = "empty key";
								break;
							default:
								value = "something went wrong";
								break;
						}
					} else {
						value = "KEY_NOT_FOUND";
					}
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					if (jedis.getResource().exists(key).booleanValue() == true) {
						keyType = jedis.getResource().type(key);
						switch (keyType) {
							case "string":
								value = jedis.getResource().get(key);
								break;
							case "list":
								value = jedis.getResource().lrange(key, 0, -1);
								break;
							case "hash":
								if (field == null) {
									obj = new JSONObject(jedis.getResource().hgetAll(key));
									value = obj;
								} else {
									try {
										value = jedis.getResource().hget(key, field);
									} catch (Exception e) {
										value = "";
									}
								}
								break;
							case "set":
								value = jedis.getResource().smembers(key);
								break;
							case "zset":
								value = jedis.getResource().zrange(key, 0, -1);
								break;
							case "none":
								value = "empty key";
								break;
							default:
								value = "something went wrong";
								break;
						}
					} else {
						value = "KEY_NOT_FOUND";
					}
					jedis.close();
					break;
			}
			return value;
		} catch (Exception error) {
			logger.error("The exception in getValues method is : " + error);
			return value;
		}
	}

	public static int Hset(String redisHost, int redisPort, String key, String field, String value, String type) {
		int noOfRecordsUpdated = 0;
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					noOfRecordsUpdated = jedisCluster.hset(key, field, value).intValue();
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					noOfRecordsUpdated = jedis.getResource().hset(key, field, value).intValue();
					jedis.close();
					break;
			}
			return noOfRecordsUpdated;
		} catch (Exception error) {
			logger.error("The exception in Hset method is : " + error);
		}
		return noOfRecordsUpdated;
	}

	public static Object getHMKeys(String redisHost, int redisPort, String key, String type) {
		Object value = null;
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					if (jedisCluster.exists(key).booleanValue() == true) {
						jedisCluster = connectToJedisCluster(redisHost, redisPort);
						value = jedisCluster.hkeys(key);
					} else {
						value = "KEY_NOT_FOUND";
					}
					jedisCluster.close();
					break;
				default:
					if (jedis.getResource().exists(key).booleanValue() == true) {
						jedis = connectToJedis(redisHost, redisPort);
						value = jedis.getResource().hkeys(key);
					} else {
						value = "KEY_NOT_FOUND";
					}
					jedis.close();
					break;
			}
			return value;
		} catch (Exception error) {
			logger.error("The exception in getHMKeys method is : " + error);
		}
		return null;
	}

	/**
	 * Delete list of keys using pattern
	 *
	 * @param redisHost
	 * @param redisPort
	 * @param type
	 * @param key
	 * @return
	 */
	public static long deleKeys(String redisHost, int redisPort, String type, String key) {
		long deletedCount = 0;
		Set<String> value;
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedis = connectToJedis(redisHost, redisPort);
					value = jedis.getResource().keys(key);
					jedis.close();
					if (value.size() > 0) {
						String[] keyList = value.toArray(new String[value.size()]);
						jedisCluster = connectToJedisCluster(redisHost, redisPort);
						int size = jedisCluster.getClusterNodes().size();
						if (keyList.length > 0) {
							for (int i = 0; i < keyList.length; i++) {
								deletedCount = deletedCount + jedisCluster.del(keyList[i]);
								if (i == 2000) {
									break;
								}
							}
						}
						jedisCluster.close();
					}
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					value = jedis.getResource().keys(key);
					if (value.size() > 0) {
						String[] keyL = value.toArray(new String[value.size()]);
						deletedCount = jedis.getResource().del(keyL);
					}
					jedis.close();
					break;
			}
		} catch (Exception error) {
			logger.error("The exception in deleKeys method is : " + error);
		}
		return deletedCount;
	}

	public static long deleKeysFromCluster(String redisHost, int redisPort, String type, String key) {
		long deletedCount = 0;
		Set<String> value;
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = NORMAL;
			}
			switch (type) {
				case CLUSTER:
					jedisCluster = connectToJedisCluster(redisHost, redisPort);
					value = jedisCluster.getClusterNodes().keySet();
					if (value.size() > 0) {
						String[] keyList = value.toArray(new String[value.size()]);
						if (keyList.length > 0) {
							for (int i = 0; i < keyList.length; i++) {
								deletedCount = deletedCount + jedisCluster.del(keyList[i]);
								if (i == 2000) {
									break;
								}
							}
						}
					}
					jedisCluster.close();
					break;
				default:
					jedis = connectToJedis(redisHost, redisPort);
					value = jedis.getResource().keys(key);
					if (value.size() > 0) {
						String[] keyL = value.toArray(new String[value.size()]);
						deletedCount = jedis.getResource().del(keyL);
					}
					jedis.close();
					break;
			}
		} catch (Exception error) {
			logger.error("The exception in deleKeysFromCluster method is : " + error);
		}
		return deletedCount;
	}

	private void init() {
		int largeTTl = 12 * 60 * 60;
		try {
			if (largeTTl > 0) {
				permanentTTl = Integer.valueOf(largeTTl);
			}
		} catch (Exception error) {
			logger.error("The exception in init method is : " + error);
		}
	}

	public JedisPoolConfig getJedisPoolConfig() {
		try {
			jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(100);
			jedisPoolConfig.setMaxTotal(100);
			return jedisPoolConfig;
		} catch (Exception error) {
			logger.error("The exception in getJedisPoolConfig method is : " + error);
			throw new RuntimeException("Jedis Pool config", error);
		}
	}
}
