package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void orderItem() throws Exception{
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문가격은 가격 * 수량이다.");
        assertEquals(8, book.getStockQuantity(),"주문 수량만큼 재고가 줄어야한다.");
    }

    @Test
    public void orderItemOutOfStock() throws Exception{
        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        //when

        //then
        assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), book.getId(), orderCount));
    }

    @Test
    public void cancerOrder() throws Exception{
        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);
        
        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL 이다.");
        assertEquals(10, book.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야한다.");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    @Test
    public void joinTest() {
        Member member = new Member();
        member.setName("test1");
        em.persist(member);

        Member member2 = new Member();
        member2.setName("test2");
        em.persist(member2);

        Member member3 = new Member();
        member3.setName("test3");
        em.persist(member3);

        Member member4 = new Member();
        member4.setName("test4");
        em.persist(member4);

        Member member5 = new Member();
        member5.setName("test5");
        em.persist(member5);


        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        em.persist(delivery);

        Book book = new Book();
        book.setName("SAdf");
        book.setPrice(10000);
        book.setStockQuantity(100);
        em.persist(book);

        OrderItem orderItem = OrderItem.createOrderItem(book, book.getPrice(), 2);
        em.persist(orderItem);

        Order order1 = Order.createOrder(member, delivery, orderItem);
        em.persist(order1);

        Order order2 = Order.createOrder(member2, delivery, orderItem);
        em.persist(order2);

        Order order3 = Order.createOrder(member3, delivery, orderItem);
        em.persist(order3);

        Order order4 = Order.createOrder(member4, delivery, orderItem);
        em.persist(order4);

        Order order5 = Order.createOrder(member5, delivery, orderItem);
        em.persist(order5);

        em.flush();
        em.clear();

        System.out.println("\n\n\n\n\n=================== SQL ======================");
        List<Order> allOrder = orderRepository.findAll(new OrderSearch());
        System.out.println("\n\n\n\n\n=================== SQL ======================");
        allOrder.stream().forEach(order -> {
            Member member1 = order.getMember();
            System.out.println(member1.getClass());
            System.out.println(member1.getName());
            System.out.println("\n\n");
        });

    }
}