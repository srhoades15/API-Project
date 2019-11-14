package edu.mtsu.csci3033;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class LanguageController {
    @Autowired
    private LanguageRepository languageRepository;

    @RequestMapping(value="/languages", method= RequestMethod.GET)
    //Function findAll returns a list of languages, No parameter is required
    //you are allowed to input a string as a parameter
    List<Languages> findAll(@RequestParam(required=false) String language){
        //generate an array list of Languages items
        List<Languages> languages = new ArrayList<>();
        //if a string language name is not passed in, or the language string name is empty
        if(language==null || language.isEmpty()){
            //create an iterable list of all of the languages in the repository
            Iterable<Languages> results = this.languageRepository.findAll();
            results.forEach(languageItem-> {languages.add(languageItem);});
        }
        //if a specific string language name IS passed in, t hen we will find by that language
        else{
            Languages languageItem = this.languageRepository.findByLanguage(language);
            //create a languageItem from the languages class, assigned to the language
            //with name matching the inputed string
            //if a match is found, add to our list of found languages
            if(null!=languageItem) {
                languages.add(languageItem);
            }
            //if a matching language is not found, return to user that the language is not in the syste
            else{
                throw new NoSuchElementException("Language (" + language + ") is not in system.");
            }
        }
        //return our list of languages
        return languages;
    }


    //Function findAll returns a list of languages, No parameter is required
        //you are allowed to input a string as a parameter
    @RequestMapping(value="/languages/getById", method= RequestMethod.GET)
    String findLanguageByID(@RequestParam(required=true) long id) {
        Languages temp = new Languages();
        if (id > 0)
        {
            temp = this.languageRepository.findById(id);
        }
        else
            throw new RuntimeException("Language could not be located or does not exist");

        return temp.getLanguage();
    }



    @RequestMapping(value="/languages", method=RequestMethod.POST, consumes="application/json")
    @ResponseStatus(value = HttpStatus.OK)
    //createLanguage is a function that takes in a string as a language name
    //and adds it to our repository  w/assigned ID
    public void createLanguage(@RequestBody(required=true) String language)
    {
        //if the parameter argument is not empty
        if (!language.isEmpty()) {
            //assign a temporary Languages object as a new object
            Languages temp = new Languages();
            try {
                //try to store the new temp into JSON and into our data set
                temp = new ObjectMapper().readValue(language, Languages.class);
            } catch (IOException e) {
                //if there is an error for any reason during runtime, an error message is kicked out to user
                throw new RuntimeException("Unable to save new language");
            }
            //search the repository to see if the temp language already exists
            if(this.languageRepository.findByLanguage(temp.getLanguage()) != null){
                throw new RuntimeException("Language already exists!");
            }
            //if language is not found, then we will insert the language
            this.languageRepository.save(temp);
        }
        //if the user doesn't specify a language, then it can't be added.
        else
        {
            throw new RuntimeException("Language to add must be specified!");
        }
    }

    @RequestMapping(value="/languages", method=RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    //update language requires a string argument that is a language name and an ID
    public void updateLanguage(@RequestParam(required=true) Long id, String language)
    {
        //if the ID is greater than 0, and the string isn't empty
        if (id > 0 && !language.isEmpty())
        {
            //create a languages object called "existing"
            Languages existing = new Languages();
            //set the existing ID to id argument and the language name to the string input
            existing.setId(id);
            existing.setLanguage(language);
            //find if existing already exists in the repository
            //if the language is already in the repository, kick out a message to the user
            if(this.languageRepository.findByLanguage(existing.getLanguage()) != null){
                throw new RuntimeException("Language already exists!");
            }
            //if language doesn't exist, save it into the repository
            this.languageRepository.save(existing);
        }
        //if the ID is not greater than 0, or, string is empty, can't update
        else
        {
            throw new NoSuchElementException("Unable to update language");
        }
    }

    @RequestMapping(value="/languages", method=RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    //function takes in a string as argument
    //the string is the name of the language we want to delete
    public void deleteLanguage(@RequestParam(required=true) String language)
    {
        //quick validation: if the string passed into the function is NOT empty
        if (!language.isEmpty())
        {
            //create a Languages object called existing
            //if that object is in the repository (found by searching the inputted
            //language name string
            try{
                Languages existing = this.languageRepository.findByLanguage(language);
                //delete the language
                this.languageRepository.delete(existing);
            }
            //if the language was not found in the repository, then we can't delete
            //and an error message is returned to user
            catch(Exception e){
                throw new NoSuchElementException("Unable to locate language");
            }
        }
        //if the string passed into the function is empty, we can't search for it
        //returns a no such element exception, and is unable to locate the empty language
        else
        {
            throw new NoSuchElementException("Unable to locate empty language");
        }
    }
}
