package com.kotlin.test.model.member

import com.kotlin.test.dto.member.MemberToAddDto
import com.kotlin.test.generated.types.MemberToAddInput
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "member")
class Member(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", columnDefinition = "int")
        var id: Long? = null,

        @Column(name = "email", length = 256)
        var email: String? = null,

        @Column(name = "name", length = 64)
        var name: String? = null,

        @Column(
                name = "created_date_time",
                columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP()",
                insertable = false, updatable = false, nullable = false
        )
        var createdDateTime: OffsetDateTime = OffsetDateTime.now(),

        @Column(
                name = "updated_date_time",
                columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP",
                insertable = false, updatable = false, nullable = false
        )
        var updatedDateTime: OffsetDateTime = OffsetDateTime.now(),
) {

        companion object {
                fun fromMemberToAddInput(input: MemberToAddInput) =
                        Member(
                                email = input.email,
                                name = input.name
                        )

                fun fromMemberToAddDto(memberToAddDto: MemberToAddDto) =
                        Member(
                                email = memberToAddDto.email,
                                name = memberToAddDto.name
                        )
        }
}