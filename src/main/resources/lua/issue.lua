-- KEYS: stock, users (set), sold_out
-- ARGV: userId, soldOutTtlSeconds
-- 반환: 1=성공, 0=매진, -1=중복 발급

if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
  return -1
end

local remaining = tonumber(redis.call('GET', KEYS[1]) or '0')
if remaining <= 0 then
  return 0
end

redis.call('DECR', KEYS[1])
redis.call('SADD', KEYS[2], ARGV[1])

if remaining == 1 then
  redis.call('SET', KEYS[3], '1', 'EX', ARGV[2])
end

return 1

-- remaining이 1이라는 것은 재고가 하나 남아잇는 상황임
-- 이후 redis.call('DECR', KEYS[1])를 통해 재고가 감소되며 키의 대한 밸류는 감소되는 상태임 하지만 remaining 자체에는 1이 남아있음
-- 그래서 매진인 상황에서 sold_out이라는 키가 왔다가 값은 임의로 1을 넣고 expiration(EX) 유효 기간 ttl을 soldOutTtlSeconds를 받아와 세팅을 하게됨