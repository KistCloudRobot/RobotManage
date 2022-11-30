# JQL (Json Query Language)

## JQL Operators
- @eq, @ne, @like, @notLike : 단일값 또는 어레이. 어레이인 경우 ('in' 또는 'or' 로 해석하여 처리)
- @gt, @lt, @ge, @le : 단일값
- @isNull: true/false 컬럼 존재 여부 확인.
- @ge_lt, @gt_lt, @ge_le, @gt_le : 범위 연산자, 2 항목 어레이.
- @or, @and : join 된 entity 내의 검색 조건을 결합할 논리 연산자 지정. (생략시 and 값 기본 적용)

## 공통 검색 parameter
- sort.
```sh
/api/v2/domain/?sort=regUser,-regDate&limit=100
# 또는 
/api/v2/domain/find?sort=regUser,-regDate&limit=100
```
```sql
select * from std_domain order by regUser, regDate desc limit 100
```

## 공통 검색 parameter
- pagination. (예, 10개 단위 2번째 페이지 검색)
```sh
/api/v2/domain/?limit=10&page=2
# 또는 
/api/v2/domain/find?limit=10&page=2
```
```sql
select * from std_domain order by regUser, regDate desc limit 100
```


## JQL 연산
- @eq, @ne : 일치값 검색 (단일 또는 다중값 지정 가능)
- @like, @notLike : 문자열 패턴 검색 (단일 또는 다중값 지정 가능)
- @gt, @lt, @ge, @le : 단일값 비교
- @isNull : true/false 로 null 값 검색.

#### 특수 operator
- @in : Join 검색을 하되, 하위 엔터티 결과를 select 하고 싶지 않을 때 사용.

### JQL 기본 구성.
* 필드명 + @연산자 : 검색값 | 하위검색 조건.
* 검색값 또는 하위 검색 조건이 Array 이면 OR 조건으로 결합.
* 중괄호{} 안의 검색 조건은 AND 조건으로 대괄호[] 안의 검색 조건은 OR 조건


## Example 1
'박'00씨가 2017년 이전에 등록한 Domain(=컬럼 데이터 속성) 검색.
```json
{
  "regUser@like": "박%",
  "regDate@lt": "2017-01-01 00:00:00"
}
```
SQL 변환 결과
```sql
select * from std_domain
where name like '박%' 
  and regDate < '2017-01-01 00:00:00' 
```

## Example 1
박** 또는 김**씨가 2017년~2018년 사이엔 등록한 Domain 검색.<br>
@eq, @like 검색값에 어레이를 지정하면, or 조건 검색.
```json
{
  "regUser@like": ["박%", "김%"],
  "regDate@ge": "2017-01-01 00:00:00",
  "regDate@lt": "2018-01-01 00:00:00"
}
# 또는
{
  "regUser@like": ["박%", "김%"],
  "regDate@ge_lt": ["2017-01-01 00:00:00", "2018-01-01 00:00:00"]
  }
}
```
SQL 변환 결과
```sql
select * from std_domain
where (name like '박%' or name like '김%')  
  and regDate >= '2017-01-01 00:00:00' 
  and regDate <  '2018-01-01 00:00:00' 
```

## Example 3
도메인의 상세 설명도 함께 출력 (자동 join 처리)
```json
{
  "baseTerm": {}
}
```
SQL
```sql
select a.*, b.* 
    from
        std_domain a 
    left outer join
        std_term b 
            on a.base_term_id=b.id 
```

## Example 4
도메인 기반 용어의 용어명에 "Enterprize"가 포함된 도메인 검색
```json
{
  "baseTerm": {
     "engName@like": "Enterprize%"
  }
}
```
SQL
```sql
select a.*, b.* 
    from
        std_domain a 
    left outer join
        std_term b 
            on a.base_term_id=b.id 
    where
        b.eng_name like 'Enterprize%'
```

## Example 5
위와 동일한 검색을 하되, baseTerm 정보는 출력할 필요가 없을 때. (@in)
```json
{
  "baseTerm@in": {
     "engName@like": "Enterprize%"
  }
}
```
SQL
```sql
select a.*
    from
        std_domain a 
    left outer join
        std_term b 
            on a.base_term_id=b.id 
    where
        b.eng_name like 'Enterprize%'
```

## Example 6
baseTerm 의 engName 에 'Enterprize' 이 포함되거나, description 에 '기술'이란 다 단어가 포함된 도메인 검
```json
{
  "baseTerm": [
    {
      "engName@like": "Enterprize%"
    },
    {
      "description@like": "기술나눔%"
    }
  ]
}
```
SQL
```sql
select a.*
    from
        std_domain a 
    left outer join
        std_term b 
            on a.base_term_id=b.id 
    where
        b.eng_name like 'Enterprize%'
    or  b.descrition like '기술%'
```
