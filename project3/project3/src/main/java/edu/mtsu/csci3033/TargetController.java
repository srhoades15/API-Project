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
public class TargetController {
    @Autowired
    private TargetRepository targetRepository;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private LanguageRepository languageRepository;

    /**
     *
     * Returns list of all targets in table, if target not passed in
     * otherwise, returns target object matching input
     * 127.0.0.1:8XXX/targets
     * or
     * 127.0.0.1:8XXX/targets?target=Hola
     */
    @RequestMapping(value="/targets", method= RequestMethod.GET)
    List<Targets> findAll(@RequestParam(required=false) String target){
        List<Targets> targets = new ArrayList<>();
        if(null==target){
            Iterable<Targets> results = this.targetRepository.findAll();
            results.forEach(wordItem-> {targets.add(wordItem);});
        }else{
            Targets targetItem = this.targetRepository.findByTarget(target);
            if(null!=targetItem) {
                targets.add(targetItem);
            }
        }
        return targets;
    }

    //This works!
    // 127.0.0.1:8XXX/targets/getById?id=1
    @RequestMapping(value="/targets/getById", method= RequestMethod.GET)
    String findTargetByID(@RequestParam(required=true) long id){
        return this.targetRepository.findById(id).orElseThrow(RuntimeException::new).getTarget();
    }


    //FIX THIS - keep same parameter names (translator)
    // 127.0.0.1:8XXX/targets/getTranslationByLanguageId?word=Hello&languageId=1&targetLanguageId=2
    @RequestMapping(value="/targets/getTranslationByLanguageId", method= RequestMethod.GET)
    String findTranslationByLanguageId(@RequestParam(required=true) String word, long languageId, long targetLanguageId){
        Targets tempTarget = new Targets();
        Words targetWord = this.wordRepository.findByWord(word);
        if (!word.isEmpty())
        {
            if(this.targetRepository.findByTarget(word) != null)
            {
                tempTarget = this.targetRepository.findByWordIdAndLanguageId(targetWord.getId(), targetLanguageId);
            }
        }
        return tempTarget.getTarget();
    }

    //FIX THIS - keep same parameter names (translator)
    // 127.0.0.1:8XXX/targets/getTranslationByLanguage?word=Hello&language=English&targetLanguage=Spanish
    @RequestMapping(value="/targets/getTranslationByLanguage", method= RequestMethod.GET)
    String findTranslationByLanguage(@RequestParam(required=true) String word, String language, String targetLanguage){
        Languages firstLanguage = this.languageRepository.findByLanguage(language);
        Languages secondLanguage = this.languageRepository.findByLanguage(targetLanguage);
        //find the target that holds the word we are translating from
        Targets tempTarget = this.targetRepository.findByTarget(word);
        long targetWordId = tempTarget.getWordId();
        Targets foundTarget = this.targetRepository.findByWordIdAndLanguageId(targetWordId, secondLanguage.getId());

        return foundTarget.getTarget();
    }



    @RequestMapping(value="/targets", method=RequestMethod.POST, consumes="application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public void createTarget(@RequestBody(required=true) String target) {
        Targets tempTarget = new Targets();
        try {
        tempTarget = new ObjectMapper().readValue(target, Targets.class);
        } catch (IOException e) {
                throw new RuntimeException("Unable to save new target");
            }
            if (this.targetRepository.findByTarget(tempTarget.getTarget()) != null) {
                throw new RuntimeException("Target already exists!");
            }
            this.targetRepository.save(tempTarget);
    }
/*
    @RequestMapping(value="/targets", method=RequestMethod.POST, consumes="application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public void createTargetWithWordIdAndLanguageId(@RequestBody(required=true) String target, long wordId, long languageId) {
        Targets tempTarget = new Targets();
        if (!target.isEmpty() && wordId > 0 && languageId > 0)
        {
            tempTarget.setLanguageId(languageId);
            tempTarget.setWordId(wordId);
        }
            try {
                tempTarget = new ObjectMapper().readValue(target, Targets.class);
            } catch (IOException e) {
                throw new RuntimeException("Unable to save new target");
            }
            if (this.targetRepository.findByTarget(tempTarget.getTarget()) != null) {
                throw new RuntimeException("Target already exists!");
        }
        this.targetRepository.save(tempTarget);
    }



    @RequestMapping(value = "/targets", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    //update Target, when the function is provided a wordId and languageId, allows the user to update both of those
    //data items within the target
    //this function will be required if a target is created without the wordId and LanguageId
    public void updateTargetWordIdAndLanguageId(@RequestParam(required = true) long id, String target,   long wordId, long languageId)
    {
    //if the ID is greater than 0, the string isn't empty, and a long was provided for both wordId and LangId
        if (id > 0 && wordId > 0 && languageId > 0)
        {
            //create a temp Words object
            Targets tempTarget = new Targets();
            tempTarget.setTarget(target);
            tempTarget.setId(id);
            tempTarget.setWordId(wordId);
            tempTarget.setLanguageId(languageId);
            //find if existing already exists in the repository
            //if the target is already in the repository, kick out a message to the user
        if (this.targetRepository.findByWordIdAndLanguageId(wordId, languageId) != null) {
            throw new RuntimeException("Target already exists with this assigned WordId and LanguageId!");
        }
        //if language doesn't exist, save it into the repository
        this.targetRepository.save(tempTarget);
    }
    //if any of the id's are not greater than 0, or, string is empty, can't update
        else {
        throw new NoSuchElementException("Unable to update Target");
    }
}
*/
    @RequestMapping(value = "/targets", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    //function takes in a string as an argument
    //the string is the name of the word we want to delete
    public void deleteTarget(@RequestParam(required = true) String target) {
        //quick validation: if the string passed into the function is NOT empty
        if (!target.isEmpty()) {
            //create a temp target object
            //find it in the repo by it's target
            try {
                Targets tempTarget = this.targetRepository.findByTarget(target);
                //delete the word
                this.targetRepository.delete(tempTarget);
            }
            //if the word was not found in the repository, then we can't delete
            //and an error message is returned to user
            catch (Exception e) {
                throw new NoSuchElementException("Unable to locate Target");
            }
        }
        //if the string passed into the function is empty, we can't search for it
        //returns a no such element exception, and is unable to locate the empty word
        else {
            throw new NoSuchElementException("Unable to locate empty Target");
        }
    }


}



