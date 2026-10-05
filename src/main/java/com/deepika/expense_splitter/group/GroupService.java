package com.deepika.expense_splitter.group;

import com.deepika.expense_splitter.exception.DuplicateResourceException;
import com.deepika.expense_splitter.exception.ResourceNotFoundException;
import com.deepika.expense_splitter.group.dto.AddMemberRequest;
import com.deepika.expense_splitter.group.dto.CreateGroupRequest;
import com.deepika.expense_splitter.group.dto.GroupResponse;
import com.deepika.expense_splitter.group.dto.MemberResponse;
import com.deepika.expense_splitter.user.User;
import com.deepika.expense_splitter.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request)
    {
        User creator= findUser(request.createdByUserId());
        Group group = groupRepository.save(
                new Group(request.name(),
                        request.description(),
                        creator));
        groupMemberRepository.save(new GroupMember(group, creator));
        return toResponse(group);
    }
    @Transactional
    public GroupResponse addMember(Long groupId, AddMemberRequest request) {
        Group group = findGroup(groupId);
        User user = findUser(request.userId());

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, user.getId())) {
            throw new DuplicateResourceException("User is already a member of this group");
        }
        groupMemberRepository.save(new GroupMember(group, user));
        return toResponse(group);
    }

    @Transactional(readOnly= true)
    public GroupResponse getGroup(Long groupId) {
        return toResponse(findGroup(groupId));
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupsForUser(Long userId) {
        findUser(userId);
        return groupMemberRepository.findByUserId(userId).stream()
                .map(GroupMember::getGroup)
                .map(this::toResponse)
                .toList();
    }

    private User findUser(Long id)
    {
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id" + id));
    }
    private Group findGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id " + id));
    }

    private GroupResponse toResponse(Group group) {
        List<MemberResponse> members = groupMemberRepository.findByGroupId(group.getId()).stream()
                .map(m -> new MemberResponse(
                        m.getUser().getId(),
                        m.getUser().getName(),
                        m.getUser().getEmail()))
                .toList();
        return new GroupResponse(group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCreatedBy().getId(),
                group.getCreatedAt(),
                members);
    }

}
