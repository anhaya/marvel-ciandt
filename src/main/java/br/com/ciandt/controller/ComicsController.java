package br.com.ciandt.controller;

import br.com.ciandt.endpoint.ComicsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/comics")
public class ComicsController {

    ComicsResource comicsResource;

    @Autowired
    public ComicsController(ComicsResource comicsResource) {
        this.comicsResource = comicsResource;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Cacheable(value = "comics")
    public ModelAndView getComics() {
        ModelAndView modelAndView = new ModelAndView("listComics");
        modelAndView.addObject("comics", comicsResource.getComics());
        return modelAndView;

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Cacheable(value = "comicsId")
    public ModelAndView getComicsId(@PathVariable("id") Integer id) {

        ModelAndView modelAndView = new ModelAndView("comicDetail");
        modelAndView.addObject("comic", comicsResource.getComicsId(id));
        return modelAndView;

    }


}
