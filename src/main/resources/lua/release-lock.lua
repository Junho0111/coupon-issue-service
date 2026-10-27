-- KEYS[1] = lock / ARGV[1] = lockToken
-- 내 token을 가진 락만 삭제한다.
if redis.call('GET', KEYS[1]) == ARGV[1] then
    return redis.call('DEL', KEYS[1])
end
return 0

-- 락 키가 set했던 락 키 그 값이 만들어 놓은 토큰값과 일치할때만 del실행