package com.advox.vue_site.controller;

import com.advox.vue_site.model.Tutorial;
import com.advox.vue_site.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {
  @Autowired
  TutorialRepository tutorialRepository;

  @GetMapping("/tutorials/id")
  public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
    Optional<Tutorial> tutorial = tutorialRepository.findById(id);
    return tutorial.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
    try {
      List<Tutorial> tutorials = new ArrayList<>();
      if (title == null)
        tutorials.addAll(tutorialRepository.findAll());
      else
        tutorials.addAll(tutorialRepository.findByTitleContaining(title));
      if (tutorials.isEmpty())
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/tutorials")
  public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
    try {
      Tutorial tutorialNew = tutorialRepository.save(new Tutorial(tutorial.getId(), tutorial.getTitle(), tutorial.getDescription(), tutorial.isPublished()));
      return new ResponseEntity<>(tutorialNew, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PutMapping("/tutorials/id")
  public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
    Optional<Tutorial> tutorialFound = tutorialRepository.findById(id);
    if (tutorialFound.isPresent()) {
      Tutorial tutorialUpdated = tutorialFound.get();
      tutorialUpdated.setTitle(tutorial.getTitle());
      tutorialUpdated.setDescription(tutorial.getDescription());
      tutorialUpdated.setPublished(tutorial.isPublished());
      return new ResponseEntity<>(tutorialRepository.save(tutorialUpdated), HttpStatus.OK);
    } else
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/tutorials/id")
  public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
    try {
      tutorialRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/tutorials")
  public ResponseEntity<HttpStatus> deleteAll() {
    try {
      tutorialRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tutorials/published")
  public ResponseEntity<List<Tutorial>> getByPublished(@RequestParam("published") boolean published) {
    try {
      List<Tutorial> tutorials = new ArrayList<>(tutorialRepository.findByPublished(published));
      if (tutorials.isEmpty())
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      else
        return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
