package th.in.nagi.fecs.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import th.in.nagi.fecs.model.User;
import th.in.nagi.fecs.model.User;
import th.in.nagi.fecs.service.UserService;

/**
 * Controller for users.
 * 
 * @author Chonnipa Kittisiriprasert
 *
 */
@Controller
@RequestMapping("/users")
public class UsersHtmlController extends BaseController {

    /**
     * User service.
     */
    @Autowired
    private UserService userService;

    /**
     * Gets user service.
     * 
     * @return user service
     */
    protected UserService getUserService() {
        return userService;
    }

    /**
     * Lists all existing users.
     * 
     * @param model
     * @return list of users
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(ModelMap model) {

        List<User> users = getUserService().findAll();
        model.addAttribute("users", users);
        return "usersList";
    }

    /**
     * Creates new user.
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = { "/new" }, method = RequestMethod.GET)
    public String create(ModelMap model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("edit", false);
        return "registration";
    }

    /*
     * This method will be called on form submission, handling POST request for
     * saving user in database. It also validates the user input
     */
    @RequestMapping(value = { "/new" }, method = RequestMethod.POST)
    public String saveUser(@Valid User user, BindingResult result,
            ModelMap model) {

        if (result.hasErrors()) {
            return "registration";
        }

        /*
         * Preferred way to achieve uniqueness of field [username] should be implementing custom @Unique annotation 
         * and applying it on field [username] of Model class [User].
         * 
         * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
         * framework as well while still using internationalized messages.
         * 
         */
        if (!getUserService().isEmailUnique(user.getId(),
                user.getEmail())) {
            FieldError usernameError = new FieldError("user", "username",
                    getMessageSource().getMessage("non.unique.username",
                            new String[] { user.getEmail() },
                            Locale.getDefault()));
            result.addError(usernameError);
            return "registration";
        }

        getUserService().store(user);

        model.addAttribute("success", "User " + user.getFirstName() + " "
                + user.getLastName() + " registered successfully");
        return "success";
    }

    /*
     * This method will provide the medium to update an existing user.
     */
    @RequestMapping(value = {
            "/edit-{username}-user" }, method = RequestMethod.GET)
    public String editUser(@PathVariable String username, ModelMap model) {
        User user = getUserService().findByEmail(username);
        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        return "registration";
    }

    /*
     * This method will be called on form submission, handling POST request for
     * updating user in database. It also validates the user input
     */
    @RequestMapping(value = {
            "/edit-{email}-user" }, method = RequestMethod.POST)
    public String updateUser(@Valid User user, BindingResult result,
            ModelMap model, @PathVariable String email) {

        if (result.hasErrors()) {
            return "registration";
        }

        if (!getUserService().isEmailUnique(user.getId(),
                user.getEmail())) {
            FieldError usernameError = new FieldError("user", "username",
                    getMessageSource().getMessage("non.unique.username",
                            new String[] { user.getEmail() },
                            Locale.getDefault()));
            result.addError(usernameError);
            return "registration";
        }

        getUserService().update(user);

        model.addAttribute("success", "User " + user.getFirstName() + " "
                + user.getLastName() + " updated successfully");
        return "success";
    }

    /*
     * This method will delete an user by it's Username value.
     */
    @RequestMapping(value = {
            "/delete-{username}-user" }, method = RequestMethod.GET)
    public String deleteUser(@PathVariable String username) {
        getUserService().removeByEmail(username);
        return "redirect:/users/list";
    }
}
