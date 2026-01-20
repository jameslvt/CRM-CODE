package com.crm.business.controller;

import com.crm.business.dto.ContactDTO;
import com.crm.business.dto.ContactFormData;
import com.crm.business.service.ContactService;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 联系人管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "联系人管理")
@RestController
@RequestMapping("/api/business/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * 分页查询联系人列表
     */
    @ApiOperation("分页查询联系人列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('business:contact:list')")
    public Result<PageResult<ContactDTO>> getContactList(
            @RequestParam Long customerId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ContactDTO> result = contactService.getContactList(customerId, name, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取客户的所有联系人
     */
    @ApiOperation("获取客户的所有联系人")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('business:contact:list')")
    public Result<List<ContactDTO>> getContactsByCustomerId(@PathVariable Long customerId) {
        List<ContactDTO> contacts = contactService.getContactsByCustomerId(customerId);
        return Result.success(contacts);
    }

    /**
     * 根据ID获取联系人详情
     */
    @ApiOperation("根据ID获取联系人详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:contact:detail')")
    public Result<ContactDTO> getContactById(@PathVariable Long id) {
        ContactDTO contact = contactService.getContactById(id);
        return Result.success(contact);
    }

    /**
     * 创建联系人
     */
    @ApiOperation("创建联系人")
    @PostMapping
    @PreAuthorize("hasAuthority('business:contact:add')")
    public Result<Long> createContact(@Validated @RequestBody ContactFormData formData) {
        Long id = contactService.createContact(formData);
        return Result.success(id);
    }

    /**
     * 更新联系人
     */
    @ApiOperation("更新联系人")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:contact:edit')")
    public Result<Void> updateContact(@PathVariable Long id, @Validated @RequestBody ContactFormData formData) {
        contactService.updateContact(id, formData);
        return Result.success();
    }

    /**
     * 删除联系人
     */
    @ApiOperation("删除联系人")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:contact:delete')")
    public Result<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return Result.success();
    }

    /**
     * 设置为主要联系人
     */
    @ApiOperation("设置为主要联系人")
    @PostMapping("/{id}/set-primary")
    @PreAuthorize("hasAuthority('business:contact:edit')")
    public Result<Void> setPrimaryContact(@PathVariable Long id) {
        contactService.setPrimaryContact(id);
        return Result.success();
    }
}
