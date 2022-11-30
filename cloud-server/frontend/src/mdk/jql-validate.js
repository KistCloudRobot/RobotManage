/**
 * @TODO 오류와 경고를 구분
 */
export function regexTest(regex, errMsg) {
    if (!errMsg) {
        errMsg = "입력오류입니다.";
    }
    return function(value) {
        return regex.test(value)? null : errMsg;
    }
}

export const regex = {
    korAndSpecialRegex : /^[가-힣!@#$%^&*)(+=._-]+$/,
    korEngNumSpecialRegex : /^[가-힣a-zA-Z!@#$%^&*)(+=._-]+$/,
    engRegex : /^[a-zA-Z_]*$/, // 언더바 허용
    engAndSpecialRegex : /^[A-Z!@#$%^&*)(+=._-~\s]+$/, // 띄어쓰기 허용
    numRegex : /^[0-9,]*$/, // 소수점 표시할 때 쉼표 필요
    domainTypeRegex : /^[ABCDNTV]{0,1}$/
}