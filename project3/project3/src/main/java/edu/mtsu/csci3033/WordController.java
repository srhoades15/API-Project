package edu.mtsu.csci3033;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class WordController {
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private LanguageRepository languageRepository;

    /**
     * Returns list of all words in table, if word not passed in
     * otherwise, returns word object matching input
     * 127.0.0.1:8XXX/words
     * or
     * 127.0.0.1:8XXX/words?word=Hello
     */
    @RequestMapping(value = "/words", method = RequestMethod.GET)
    List<Words> findAll(@RequestParam(required = false) String word) {
        List<Words> words = new ArrayList<>();
        if (null == word) {
            Iterable<Words> results = this.wordRepository.findAll();
            results.forEach(wordItem -> {
                words.add(wordItem);
            });
        } else {
            Words wordItem = this.wordRepository.findByWord(word);
            if (null != wordItem) {
                words.add(wordItem);
            }
        }
        return words;
    }


    @RequestMapping(value="/words/getById", method= RequestMethod.GET)
    String findWordByID(@RequestParam(required=true) long id){
        return this.wordRepository.findById(id).orElseThrow(RuntimeException::new).getWord();
    }


    @RequestMapping(value = "/words", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    //createWord is a function that takes in a string as a wordID
    //and adds it to our repository  w/assigned ID
    public void createWord(@RequestBody(required = true) String word) {
        //if the parameter argument is not empty
        if (!word.isEmpty()) {
            //assign a temporary Words object as a new object
            Words temp = new Words();
            try {
                //try to store the new temp into JSON and into our data set
                temp = new ObjectMapper().readValue(word, Words.class);
            } catch (IOException e) {
                //if there is an error for any reason during runtime, an error message is kicked out to user
                throw new RuntimeException("Unable to save new Word");
            }
            //search the repository to see if the temp Word already exists
            if (this.wordRepository.findByWord(temp.getWord()) != null) {
                throw new RuntimeException("Language already exists!");
            }
            //if language is not found, then we will insert the Word
            this.wordRepository.save(temp);
        }
        //if the user doesn't specify a word, then it can't be added.
        else {
            throw new RuntimeException("Word to add must be specified!");
        }
    }


    @RequestMapping(value = "/words", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    //updateWordByID is a PUT / Update function that lets user input an id for a word,
    //and an updated wordID as a string
    public void updateWord(@RequestParam(required = true) long id, String word) {
        //if the ID is greater than 0, and the string isn't empty
        if (id > 0 && !word.isEmpty()) {
            //create a temp Words object
            Words temp = new Words();
            temp.setId(id);
            temp.setWord(word);
            //set the existing ID to id argument and the language name to the string input
            //find if existing already exists in the repository
            //if the language is already in the repository, kick out a message to the user
            if (this.wordRepository.findByWord(temp.getWord()) != null) {
                throw new RuntimeException("Word already exists!");
            }
            //if language doesn't exist, save it into the repository
            this.wordRepository.save(temp);
        }
        //if the ID is not greater than 0, or, string is empty, can't update
        else {
            throw new NoSuchElementException("Unable to update Word");
        }
    }


    @RequestMapping(value = "/words", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    //function takes in a string as an argument
    //the string is the name of the word we want to delete
    public void deleteWordByWord(@RequestParam(required = true) String word) {
        //quick validation: if the string passed into the function is NOT empty
        if (!word.isEmpty()) {
            //create a Words object called existing
            //if that object is in the repository (found by searching the inputted
            //language name string
            try {
                Words temp = this.wordRepository.findByWord(word);
                //delete the word
                this.wordRepository.delete(temp);
            }
            //if the word was not found in the repository, then we can't delete
            //and an error message is returned to user
            catch (Exception e) {
                throw new NoSuchElementException("Unable to locate Word");
            }
        }
        //if the string passed into the function is empty, we can't search for it
        //returns a no such element exception, and is unable to locate the empty word
        else {
            throw new NoSuchElementException("Unable to locate empty Word");
        }
    }




    //ADD OTHER STUFF HERE (POST/PUT/DELETE && MORE GETS)

}



