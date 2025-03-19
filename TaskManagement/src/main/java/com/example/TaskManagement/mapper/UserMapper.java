package com.example.TaskManagement.mapper;

import com.example.TaskManagement.model.entity.User;
import com.example.TaskManagement.model.request.UpsertUserRequest;
import com.example.TaskManagement.model.response.UserListResponse;
import com.example.TaskManagement.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User requestToUser(UpsertUserRequest request);

    @Mapping(source = "userId", target = "id")
    User requestToUser(Long userId, UpsertUserRequest request);

    UserResponse userToResponse(User user);

    default UserListResponse userListToUserResponseList(List<User> users) {
        UserListResponse response = new UserListResponse();
        response.setUsers(users.stream().map(this::userToResponse).collect(Collectors.toList()));

        return response;
    }
}
