package com.example.TaskManagement.aspect;

import com.example.TaskManagement.exception.AccessDeniedException;
import com.example.TaskManagement.model.entity.Task;
import com.example.TaskManagement.model.request.UpsertTaskRequest;
import com.example.TaskManagement.security.AppUserDetails;
import com.example.TaskManagement.service.TaskService;
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

    @Before("execution(* com.example.springboottaskportal.web.controller.TaskController.update(..)) && args(taskId, taskDto)")
    public void checkUpdateAccess(Long taskId, UpsertTaskRequest taskDto) throws AccessDeniedException {
        validateAccess(taskId);
    }

    @Before("execution(* com.example.springboottaskportal.web.controller.TaskController.delete(..)) && args(taskId)")
    public void checkDeleteAccess(Long taskId) throws AccessDeniedException {
        validateDeleteAccess(taskId);
    }

    private void validateAccess(Long taskId) throws AccessDeniedException {
        Task task = taskService.findById(taskId);

        if (task == null || !taskService.findById(taskId).getAssignee().getId().equals(findUserId())) {
            throw new AccessDeniedException("You do not have permission to access this task item.");
        }
    }

    private void validateDeleteAccess(Long taskId) throws AccessDeniedException {
        Task task = taskService.findById(taskId);

        if (task == null) {
            throw new AccessDeniedException("There's no task with such id.");
        }
        Long userId = findUserId();
        if(userId != null && !task.getAssignee().getId().equals(userId)){
            if ((hasRole("ROLE_ADMIN") || hasRole("ROLE_MODERATOR"))){
                return;
            }
            throw new AccessDeniedException("You do not have permission to access this task item.");
        }
    }
    private boolean hasRole(String roleName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails){
            return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(roleName));
        }
        return false;
    }
    private Long findUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.getPrincipal() instanceof UserDetails userDetails &&
                userDetails instanceof AppUserDetails appUserDetails) {
            return appUserDetails.getId();
        }
        return null;
    }
}
