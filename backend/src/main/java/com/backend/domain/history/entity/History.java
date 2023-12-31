package com.backend.domain.history.entity;

import com.backend.domain.common.BaseEntity;
import com.backend.domain.history.constant.HistoryType;
import com.backend.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    private Long drawingId;

    private Long sendId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryType historyType;

    public History (Member member, String content, Long drawingId,Long sendId, HistoryType historyType){
        this.member = member;
        this.content = content;
        this.drawingId = drawingId;
        this.sendId = sendId;
        this.historyType = historyType;
    }

}
