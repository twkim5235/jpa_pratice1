# 실전! 스프링 부트와 JPA 활용1

참고 강의 사이트:https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1/dashboard



프로젝트에서 사용하는 라이브러리

- spring web
- spring data jpa
- thymeleaf
- lombok
- H2 database driver
- spring mc
- spring orm
- JPA, Hibernate



### Thymleaf

**thymeleaf는 템플릿 엔진이다**

- 스프링 부트 thymeleaf viewName 매핑
  - `resources:templates` + {ViewName} + `html`



### 도메인 모델과 테이블 설계

![](./picture/도메인_모델.png)



![](./picture/DB_구조도.png)



### 애플리케이션 아키텍쳐

![](./picture/애플리케이션_아키텍쳐.png)



### 엔티티 설계 시 주의점



#### 엔티티에는 가급적 Setter를 사용하지 말자

Setter가 모두 열려있으면 변경 포인트가 너무 많아서, 유지보수가 어렵다.



#### 모든 연관관계는 지연로딩으로 설정!

- 즉시로딩(`EAGER`)는 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행할 때 N+1 문제가 자주 발생한다.
- 실무에서 모든 연관관계는 지연로딩(`LAZY`)으로 설정해야 한다
- 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join또는 엔티티 그래프 기능을 사용한다.
- @XToOne(OneToOne, ManyToOne)관계는 기본이 즉시로딩이므로 직접 지연로딩을 설정해야 한다.



#### 컬렉션은 필드에서 초기화 하자.

- 컬렉션은 필드에서 바로 초기화 하는 것이 안전하다.
- `null` 문제에서 안전하다.
- 하이버네이트는 엔티티를 영속화 할 때, 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한다. 만약 `getOrders()`처럼 임의의 메소드에서 컬렉션을 잘못 생성하면 하이버네이트 내부 매커니즘에 문제가 발생할 수 있다. 따라서 필드레벨에서 생성하는 것이 가장 안전하고, 코드도 간결하다.



#### 테이블 컬럼명 생성전략

- 스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다름

- 하이버네이트 기존 구현: 엔티티 필드명을 그대로 테이블 명으로 사용
  - (`SpringPhysicalNamingStrategy`)

- 스프링 부트 신규 설정(엔티티(필드) -> 테이블(컬럼))
  - 카멜 케이스 -> 스네이크케이스(memberPotint -> member_point)
  - .(점) -> _(언더스코어)
  - 대문자 -> 소문자



#### 연관관계 편의 메서드

- 연관관계가 맺어있는 데이터를 영속할 때 단순히 연관관계의 주인인 Data만 저장해도 상관은 없지만, 객체를 다누는 것이고, 또한 양방향으로 연관관계를 맺어놨기 때문에 반대편에도 데이터가 저장하게 구현한다.
- 연관관계 편의 메서드를 구현하는 곳은 주로 사용하는 곳에 저장해두는 것이 좋다.

~~~java
		//==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDeliver(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
~~~



