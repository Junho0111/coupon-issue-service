-- KEYS[1] = coupon:{id} (Hash: value, fetchedAtMs), KEYS[2] = lock key
-- ARGV: nowMs, freshMs, lockToken, lockTtlMs
-- 반환: HIT (값 반환) | STALE_REFRESH (값 반환 + 백그라운드 refresh)
--        | LOAD (이 요청이 채움) | WAIT (다른 요청이 채우는 중)

local data = redis.call('HMGET', KEYS[1], 'value', 'fetchedAtMs')
local value, fetchedAtStr = data[1], data[2]

if not value then
    local acquired = redis.call('SET', KEYS[2], ARGV[3], 'NX', 'PX', ARGV[4])
    if acquired then return {'LOAD', ''} end
    return {'WAIT', ''}
end

-- 현재 시간과 캐시를 기록했을때 시간을 빼서 fetchedAt이것이 우리가 정학 특정 값보다 작으면 HIT인것임
-- 만약 이 시간이 너무 오래되었다면 아래 리프레시를 시도함
local now = tonumber(ARGV[1])
local fetchedAt = tonumber(fetchedAtStr)
if now - fetchedAt < tonumber(ARGV[2]) then
    return {'HIT', value}
end

-- stale: refresh lock 시도. 못 잡으면 다른 요청이 갱신 중이니 그냥 HIT.
-- refresh Lock을 새로 만들어 Lock키를 만듬 그래서 이번에도 요청받은 최초 요청만 STALE_REFRESH 응답을 받고 나머지느 HIT이라는 응답을 받음
local acquired = redis.call('SET', KEYS[2], ARGV[3], 'NX', 'PX', ARGV[4])
if acquired then return {'STALE_REFRESH', value} end
return {'HIT', value}

-- 이전에는 키에다가 value만 넣었지만 이번에는 fetchedAtMs까지 넣음 그래서 fetchedAtMs가 오래됐다고 판단이 되면 백그라운드에서 리플래시를 할것(DB로 부터 캐시로 새로운 값을 채움)
-- 아래 if 문은 캐시가 없을 떄 새로 하는것이며 이전의 single flight와 같음
