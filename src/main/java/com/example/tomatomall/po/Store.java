package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    @Basic
    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId; // 指向 account.id

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "status")
    private String status = "ACTIVE";

    @Basic
    @Column(name = "create_time")
    private Date createTime;
}


