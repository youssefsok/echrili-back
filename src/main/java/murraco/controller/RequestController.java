package murraco.controller;

import murraco.dto.FriendRequestResponse;
import murraco.model.Request;
import murraco.model.User;
import murraco.service.RequestService;
import murraco.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;



    @GetMapping("/add/{id}")
    public void sendFriendRequest(HttpServletRequest req, @PathVariable int id) {
//        User firstUser = userService.whoami(req);
//        User secondUser = userService.getUserById(id);
//        Request addFriendRequest = new Request();
//        addFriendRequest.setFirstUser(firstUser);
//        addFriendRequest.setSecondUser(secondUser);
        User firstUser = userService.whoami(req);
        User secondUser = userService.getUserById(id);
        Request friendRequest = new Request();
        friendRequest.setFirstUser(firstUser);
        friendRequest.setSecondUser(secondUser);
        requestService.save(friendRequest);

    }

    @GetMapping("/accept/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public void acceptFriendRequest(HttpServletRequest req, @PathVariable("id") int id) {
        User whoami = userService.whoami(req);
        User secondUser = userService.getUserById(id);
        System.out.println(whoami);
        System.out.println(secondUser);

        Request friendRequest = requestService.getRequestByFirstUserAndSecondUser(secondUser, whoami);
        if(friendRequest!=null && friendRequest.getSecondUser().getId()==whoami.getId()) {
            friendRequest.setStatus(true);
            requestService.save(friendRequest);
        }
        else
        {

        }


    }



    @GetMapping("/requests")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public List<FriendRequestResponse> getAllFriendRequests(HttpServletRequest req) {
        User whoami = userService.whoami(req);

        List<Request> requestList = requestService.getRequestsBySecondUserAndStatusIsFalse(whoami);
        List<FriendRequestResponse> friendRequestResponses=new ArrayList<>();
        for (Request request : requestList) {
            System.out.println(request.getFirstUser().getUsername());
            friendRequestResponses.add(new FriendRequestResponse(request.getId(),request.getFirstUser()));
        }

        return friendRequestResponses;
    }


}