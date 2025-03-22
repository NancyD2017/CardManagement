package com.example.task_management.aspect;

import com.example.task_management.exception.AccessDeniedException;
import com.example.task_management.model.entity.Role;
import com.example.task_management.model.entity.Task;
import com.example.task_management.model.entity.User;
import com.example.task_management.model.request.UpsertPutRequest;
import com.example.task_management.security.AppUserDetails;
import com.example.task_management.service.TaskService;
import com.example.task_management.service.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TaskAccessAspect {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    @Before("execution(* com.example.task_management.controller.TaskController.update(..)) && args(taskId, statusDto)")
    public void checkStatusAccess(Long taskId, UpsertPutRequest statusDto) throws AccessDeniedException {
        validateAccess(taskId);
    }

    @Before("execution(* com.example.task_management.controller.TaskController.addComment(..)) && args(taskId, commentDto)")
    public void checkCommentAccess(Long taskId, UpsertPutRequest commentDto) throws AccessDeniedException {
        validateAccess(taskId);
    }

    private void validateAccess(Long taskId) throws AccessDeniedException {
        Task task = taskService.findById(taskId);
        Long id = findUserId();
        User user = userService.findById(id);
        if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
            if (task.getAssignee() == null || !task.getAssignee().getId().equals(id)) {
                throw new AccessDeniedException("You do not have permission to access this task item.");
            }
        }
    }

    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(roleName));
        }
        return false;
    }

    private Long findUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.getPrincipal() instanceof UserDetails userDetails &&
                userDetails instanceof AppUserDetails appUserDetails) {
            return appUserDetails.getId();
        }
        return null;
    }
}
