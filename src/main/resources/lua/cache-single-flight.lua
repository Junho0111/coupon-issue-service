-- KEYS: cache, lock / ARGV: lockToken, lockTtlMs
-- 반환: HIT | LOAD | WAIT (대기 필요)

local cached = redis.call('GET', KEYS[1])
if cached then return {'HIT', cached} end

local acquired = redis.call('SET', KEYS[2], ARGV[1], 'NX', 'PX', ARGV[2])
if acquired then return {'LOAD', ''} end
return {'WAIT', ''}

-- 캐시 키의 저장된 정보를 조회했을때 있으면 리턴을 한번 하고 git 이랑 캐시 밸류값을 리턴
-- 없으면 예를들어 500건의 요청중 처음 온 요청이 저기 들어가서 키가 있는지 없는지 확인하고 없으면 키를 만(ttl)을 붙여서 키를 저장함
-- 그래서 일종의 lock이 잡히면 최초 명령어만 들어가고 그 다음 명령어들은 여기서 false를 반환하고 그냥 return Wailt으로 감
-- 하지만 처음 성공한건 load를 반환함

-- 일조의 lock잡힌다고한 이유 => 키가 있으면 이 명령어를 실행하지 않고 wait을 가져오기때문