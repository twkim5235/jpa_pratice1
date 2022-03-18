package jpabook.jpashop.domain;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.domain.item.Item;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CategoryItem {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
