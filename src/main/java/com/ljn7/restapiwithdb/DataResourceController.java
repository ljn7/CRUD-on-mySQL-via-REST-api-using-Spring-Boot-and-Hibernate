package com.ljn7.restapiwithdb;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataResourceController {

    @Autowired
    SqlDataController datacontroller;

    private static final String NOT_AN_ALPHABET_REGEX = "[^a-zA-Z]";
    private static final String NOT_AN_ALPHABET_FOR_NAME_REGEX = "[^a-zA-Z ]";
    private static final String NOT_A_NUMER_REGEX = "[^0-9]";
    private static final String NOT_A_NUMBER_WITH_HYPEN_REGEX = "[^0-9-]";

    @GetMapping({ "users", "user" })
    public ResponseEntity<List<User>> getUsers() {

        if (datacontroller.findAll().isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

        return new ResponseEntity<>((List<User>) datacontroller
                .findAll(), HttpStatus.OK);
    }

    @PostMapping("user/add")
    public ResponseEntity<User> addUser(@RequestParam String name,
            @RequestParam String age,
            @RequestParam String gender) {

        User user = null;
        if (name.isBlank() ||
                age.isBlank() ||
                gender.isBlank())
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        if (Pattern.compile(NOT_AN_ALPHABET_FOR_NAME_REGEX).matcher(name).find() ||
                Pattern.compile(NOT_A_NUMER_REGEX).matcher(age).find() ||
                Pattern.compile(NOT_AN_ALPHABET_REGEX).matcher(gender).find())
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        int count = (int) name.chars().filter(ch -> ch == ' ').count();

        if (count > 1)
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        int charAtIndex = name.indexOf(' ');
        if ((charAtIndex == (name.length() - 1)) ||
                charAtIndex == 0)
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        if (charAtIndex != -1)
            name = (name.substring(0, charAtIndex + 1) +
                    Character.toUpperCase(name.charAt(charAtIndex + 1)) +
                    name.substring(charAtIndex + 2));

        try {
            if (Integer.MAX_VALUE < Long.parseLong(age) ||
                    Integer.parseInt(age) <= 0 ||
                    Integer.parseInt(age) >= 421)
                return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        gender = gender.substring(0, 1).toUpperCase() + gender.substring(1);

        user = new User();
        user.setName(name);
        user.setGender(gender);
        try {
            user.setAge(Integer.parseInt(age));
            datacontroller.save(user);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("user/delete")
    public ResponseEntity<User> deleteUser(@RequestParam String id) {

        User user = null;

        if (id.isBlank())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        if (Pattern.compile(NOT_A_NUMER_REGEX).matcher(id).find())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        try {
            if (Integer.MAX_VALUE < Long.parseLong(id) ||
                    Integer.parseInt(id) < 0)
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            user = (User) datacontroller.findById(Integer.parseInt(id)).get().clone();
            datacontroller.deleteById(Integer.parseInt(id));

        } catch (NumberFormatException | CloneNotSupportedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("user/update")
    public ResponseEntity<User> updateUser(@RequestParam String id,
            @RequestParam String name,
            @RequestParam String age,
            @RequestParam String gender) {
        User user = null;

        if (name.isBlank() ||
                age.isBlank() ||
                gender.isBlank() ||
                id.isBlank())
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        if (Pattern.compile(NOT_A_NUMER_REGEX).matcher(id).find() ||
                Pattern.compile(NOT_AN_ALPHABET_FOR_NAME_REGEX).matcher(name).find() ||
                Pattern.compile(NOT_A_NUMER_REGEX).matcher(age).find() ||
                Pattern.compile(NOT_AN_ALPHABET_REGEX).matcher(gender).find())
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        int count = (int) name.chars().filter(ch -> ch == ' ').count();

        if (count > 1)
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        int charAtIndex = name.indexOf(' ');
        if ((charAtIndex == (name.length() - 1)) ||
                charAtIndex == 0)
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

        if (charAtIndex != -1)
            name = (name.substring(0, charAtIndex + 1) +
                    Character.toUpperCase(name.charAt(charAtIndex + 1)) +
                    name.substring(charAtIndex + 2));

        try {
            if (Integer.MAX_VALUE < Long.parseLong(id) ||
                    Integer.parseInt(id) <= 0 ||
                    Integer.MAX_VALUE < Long.parseLong(age) ||
                    Long.parseLong(age) <= 0)
                return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

            user = datacontroller.findById(Integer.parseInt(id)).get();

            if (user == null)
                return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);

            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            gender = gender.substring(0, 1).toUpperCase() + gender.substring(1);
            user.setName(name);
            user.setAge(Integer.parseInt(age));
            user.setGender(gender);
            datacontroller.save(user);

            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("user/search/name")
    public ResponseEntity<List<User>> findUserByName(@RequestParam(required = false) String name) {

        List<User> user = null;

        if (name.isBlank())
            return getUsers();

        if (Pattern.compile(NOT_AN_ALPHABET_FOR_NAME_REGEX).matcher(name).find())
            return getUsers();

        try {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            user = datacontroller.findByName(name);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("user/search/age")
    public ResponseEntity<List<User>> findUserByAge(@RequestParam(required = false) String age) {

        System.out.println("1");
        if (age.isBlank())
            return getUsers();

        Pattern pattern = Pattern.compile(NOT_A_NUMBER_WITH_HYPEN_REGEX);
        int charPos = 0;
        int charHypenCount = 0;

        charHypenCount = (int) age.chars().filter(ch -> ch == '-').count();

        System.out.println("2");
        // if contains more than 1 hypen and Negation of numbers and hypen then return.
        if (charHypenCount > 1 ||
                pattern.matcher(age).find())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        charPos = age.indexOf('-');

        System.out.println("3");
        // if only hypen contains in age AND age len is equals to one OR
        // if Hypen is at index 0 and followed by characters OR if Hypen is at index End
        // then return bad req
        if ((charPos != -1 && age.length() == 1) ||
                (charPos == 0 || (charPos == age.length() - 1)))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        try {
            pattern = Pattern.compile("-");
            System.out.println("4");
            // if age value is less than 0 or greater IntegerMAX return BadReqest
            if (!(pattern.matcher(age).find()))
                if (Integer.MAX_VALUE < Long.parseLong(age) ||
                        Integer.parseInt(age) <= 0)
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

            // if contains only number
            System.out.println("5");
            if (!pattern.matcher(age).find()) {
                System.out.println("Inside 5th block");
                int index;
                index = Integer.parseInt(age);
                return new ResponseEntity<>(datacontroller
                        .findByAge(index, index), HttpStatus.OK);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("6");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        System.out.println("Outside fifth block");

        int indexOne;
        int indexTwo;
        String tempVar = "";

        try {

            for (int i = 0; i < charPos; i++)
                tempVar += age.charAt(i);

            System.out.println("7");

            if (Long.parseLong(tempVar) > Integer.MAX_VALUE)
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);// BR

            indexOne = Integer.parseInt(tempVar);
            tempVar = "";

            for (int i = (charPos + 1); i < age.length(); i++)
                tempVar += age.charAt(i);

            System.out.println("8");
            if (Long.parseLong(tempVar) > Integer.MAX_VALUE)
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);// BR

            indexTwo = Integer.parseInt(tempVar);

            System.out.println("9");
            return new ResponseEntity<>(((List<User>) datacontroller
                    .findByAge(indexOne, indexTwo)), HttpStatus.OK);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        System.out.println("10");
        return getUsers();
    }

}
