package com.auto.common.utils.db.cache;


import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
		} catch (Exception e) {
			e.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
			return value;
		}
	}

	public static int Hset(String redisHost, int redisPort, String key, String field, String value, String type) {
		int noOfRecordsUpdated = 0;
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
		}
		return noOfRecordsUpdated;
	}

	public static Object getHMKeys(String redisHost, int redisPort, String key, String type) {
		Object value = null;
		try {
			JedisPool jedis = null;
			JedisCluster jedisCluster = null;
			if (type == null) {
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
					System.out.println("values  *****   : " + value.toString());
					if (value.size() > 0) {
						String[] keyL = value.toArray(new String[value.size()]);
						deletedCount = jedis.getResource().del(keyL);
					}
					jedis.close();
					break;
			}
		} catch (Exception error) {
			error.printStackTrace();
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
				type = "normal";
			}
			switch (type) {
				case "cluster":
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
					System.out.println("values  *****   : " + value.toString());
					if (value.size() > 0) {
						String[] keyL = value.toArray(new String[value.size()]);
						deletedCount = jedis.getResource().del(keyL);
					}
					jedis.close();
					break;
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
		return deletedCount;
	}

	private void init() {
		int largeTTl = 12 * 60 * 60;
		try {
			if (largeTTl > 0) {
				permanentTTl = Integer.valueOf(largeTTl);
			}
		} catch (Exception e) {
		}
	}

	public JedisPoolConfig getJedisPoolConfig() {
		try {
			jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(100);
			jedisPoolConfig.setMaxTotal(100);
			return jedisPoolConfig;
		} catch (Exception e) {
			throw new RuntimeException("Jedis Pool config", e);
		}
	}
}
