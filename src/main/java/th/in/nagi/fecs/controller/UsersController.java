package th.in.nagi.fecs.controller;

import java.lang.annotation.Retention;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.in.nagi.fecs.message.ErrorMessage;
import th.in.nagi.fecs.message.Message;
import th.in.nagi.fecs.message.SuccessMessage;
import th.in.nagi.fecs.model.User;
import th.in.nagi.fecs.model.User;
import th.in.nagi.fecs.service.AuthenticateService;
import th.in.nagi.fecs.service.RoleService;
import th.in.nagi.fecs.service.UserService;

/**
 * Controller for users.
 * 
 * @author Nara Surawit
 *
 */
@Controller
@RequestMapping("/api/user")
public class UsersController extends BaseController {

    /**
     * User service.
     */
    @Autowired
    private UserService userService;
    
    /**
     * authenticate service.
     */
    @Autowired
    private AuthenticateService authenticateService;
    
    /**
     * role service
     */
    @Autowired
    private RoleService roleService;

    /**
     * Gets user service.
     * @return user service
     */
    protected UserService getUserService() {
        return userService;
    }

    /**
     * Lists all existing users. 
     * @return list of users
     */
    @ResponseBody
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Message getAllUsers(@RequestHeader(value = "token") String token) {
		if (!authenticateService.checkPermission(token, authenticateService.STAFF, authenticateService.MANAGER,
				authenticateService.OWNER)) {
			return new ErrorMessage(Message.ERROR, "This user does not allow", "401");
		}
        Set<User> users = new HashSet<User>(getUserService().findAll());
        if(users != null) {
			return new SuccessMessage(Message.SUCCESS, users, "200");
		}
		return new ErrorMessage(Message.ERROR, "Not found user.", "400");
    }
    
    /**
	 * list of user with limit size
	 * 
	 * @param start
	 *            position of the list
	 * @param size
	 *            size of the list
	 * @return limit list of user
	 */
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Message getListUsers(@RequestParam(value = "start", required = false) int start,
			@RequestParam(value = "size", required = false) int size,@RequestHeader(value = "token") String token) {
		if (!authenticateService.checkPermission(token, authenticateService.STAFF, authenticateService.MANAGER,
				authenticateService.OWNER)) {
			return new ErrorMessage(Message.ERROR, "This user does not allow", "401");
		}
		int userListSize = userService.findAll().size();
		if (size > userListSize - start) {
			size = userListSize - start;
		}
		System.out.println(userListSize);
		System.out.println(start+"                                                   "+size);
		List<User> user = (userService.findAndAscByFirstName(start, size));
		if (user == null) {
			return new ErrorMessage(Message.ERROR, "Not found user.", "400");
		}
		return new SuccessMessage(Message.SUCCESS, user, "200");
	}
	
    
    /**
     * get user by email
     * @param email email of user that want to show
     * @return user if not return message fail
     */
    @ResponseBody
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET)
    public Message getUserByEmail(@PathVariable String email, @RequestHeader(value = "token") String token) {
		if (!authenticateService.checkPermission(token, authenticateService.STAFF, authenticateService.MANAGER,
				authenticateService.OWNER)) {
			return new ErrorMessage(Message.ERROR, "This user does not allow", "401");
		}

        User user = getUserService().findByEmail(email);
        if(user != null) {
			return new SuccessMessage(Message.SUCCESS, user, "200");
		}
		return new ErrorMessage(Message.ERROR, "Not found user.", "400");
    }

    /**
     * Creates new user.
     * 
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = { "/new" }, method = RequestMethod.POST)
    public Message create(@RequestBody User user, @RequestParam(value = "roleId", required = false)int id) {
    	Date date = new Date();
    	String passwordHash = user.changeToHash(user.getPassword());
    	user.setPassword(passwordHash);
    	user.setJoiningDate(date);
    	user.setRole(roleService.findByKey(id));
    	System.out.println(user);
    	try {
			getUserService().store(user);
		} catch (Exception e) {
			System.out.println(e);
			return new ErrorMessage(Message.ERROR, "Create user failed", "400");
		}
		return new SuccessMessage(Message.SUCCESS, user, "201");
    }
    

    /**
     * edit user by member
     * @param newUser put user information that want to change, it is not required all parameter of user. 
     * @return message message and email of user or not return message fail and string "not found"
     */
    @ResponseBody
    @RequestMapping(value = {"/editByMember" }, method = RequestMethod.PUT)
    public Message editUserByMember(@RequestBody User newUser, @RequestHeader(value = "token") String token) {
		if (!authenticateService.checkPermission(token, authenticateService.MEMBER)) {
			return new ErrorMessage(Message.ERROR, "This user does not allow", "401");
		}
//        User user = getUserService().findByUsername(newUser.getUsername());
    	try {
    		getUserService().update(newUser);
		} catch (Exception e) {
			return new ErrorMessage(Message.ERROR, "User not found", "400");
		}
		return new SuccessMessage(Message.SUCCESS, getUserService().findByEmail(newUser.getEmail()).getEmail(), "200");
    }
    
    /**
     * edit user by admin
     * @param newUser put user information that want to change, it is not required all parameter of user. 
     * @return message message and email of user or not return message fail and string "not found"
     */
    @ResponseBody
    @RequestMapping(value = {"/editByAdmin" }, method = RequestMethod.PUT)
    public Message editUserByAdmin(@RequestBody User newUser, @RequestHeader(value = "token") String token) {
		if (!authenticateService.checkPermission(token, authenticateService.STAFF, authenticateService.MANAGER,
				authenticateService.OWNER)) {
			return new ErrorMessage(Message.ERROR, "This user does not allow", "401");
		}
		if(!authenticateService.getRole(token).getName().equals(authenticateService.OWNER)){
			newUser.setRole(null);
		}
//        User user = getUserService().findByUsername(newUser.getUsername());
    	try {
    		getUserService().update(newUser);
		} catch (Exception e) {
			return new ErrorMessage(Message.ERROR, "User not found", "400");
		}
		return new SuccessMessage(Message.SUCCESS, getUserService().findByEmail(newUser.getEmail()).getEmail(), "200");
    }
    /*
     * This method will delete an user by it's Username value.
     */
    /**
     * delete a user by id
     * @param tempUser username and password
     * @return message message success if not return message fail
     */
    @ResponseBody
    @RequestMapping(value = {"/delete" }, method = RequestMethod.DELETE)
    public Message deleteUser(@RequestBody User tempUser, @RequestHeader(value = "token") String token) {
		if (!authenticateService.checkPermission(token, authenticateService.OWNER)) {
			return new ErrorMessage(Message.ERROR, "This user does not allow", "401");
		}
    	User user = getUserService().findByEmail(tempUser.getEmail());
    	String passwordHash = user.changeToHash(tempUser.getPassword());
    	
    	if(!user.getPassword().equals(passwordHash)){
    		return new ErrorMessage(Message.ERROR, "Incorrect password", "400");
    	}
       
        try {
        	getUserService().removeByEmail(user.getEmail());
		} catch (Exception e) {
			return new ErrorMessage(Message.ERROR, "User not found", "400");
		}
		return new SuccessMessage(Message.SUCCESS, user.getEmail() +" has removed", "200");
    }
 }
