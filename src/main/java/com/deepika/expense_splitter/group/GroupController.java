package com.deepika.expense_splitter.group;

import com.deepika.expense_splitter.group.dto.AddMemberRequest;
import com.deepika.expense_splitter.group.dto.CreateGroupRequest;
import com.deepika.expense_splitter.group.dto.GroupResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;
    public GroupController(GroupService groupService)
    {
        this.groupService=groupService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request)
    {
        return groupService.createGroup(request);
    }
    @GetMapping("/{id}")
    public GroupResponse getGroup(@PathVariable Long id)
    {
        return groupService.getGroup(id);
    }
    @GetMapping
    public List<GroupResponse> getGroupsForUser(@RequestParam Long userId)
    {
        return groupService.getGroupsForUser(userId);
    }
    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse addMember(@PathVariable Long id,
                                   @Valid @RequestBody AddMemberRequest request) {
        return groupService.addMember(id, request);
    }
}
