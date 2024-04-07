package com.kotlin.test.controller

import com.kotlin.test.dto.member.MemberDto
import com.kotlin.test.dto.member.MemberToAddDto
import com.kotlin.test.model.member.Member
import com.kotlin.test.service.MemberService
import com.kotlin.test.util.alsoIfTrue
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException

@RestController
@RequestMapping(value = ["/api/v1/members"])
class MemberController(
        private val memberService: MemberService,
) {

    @RequestMapping(method = [RequestMethod.POST], value = [""])
    fun addMember(@RequestBody memberToAddDto: MemberToAddDto): ResponseEntity<*> {
        verifyForAddingMember(memberToAddDto = memberToAddDto)

        return memberService.save(member = Member.fromMemberToAddDto(memberToAddDto = memberToAddDto))
                .let {
                    ResponseEntity(MemberDto.fromMember(member = it), HttpStatus.CREATED)
                }
    }

    private fun verifyForAddingMember(memberToAddDto: MemberToAddDto) {
        memberService.existsByEmail(email = memberToAddDto.email)
                .alsoIfTrue { throw IllegalArgumentException("Already Exists Email") }
    }

    @RequestMapping(method = [RequestMethod.GET], value = ["/{memberId}"])
    fun getById(@PathVariable memberId: Long): ResponseEntity<*> {
        return memberService.getMemberDtoById(memberId)
                .let { memberDto ->
                    ResponseEntity(memberDto, HttpStatus.OK)
                }
    }
}