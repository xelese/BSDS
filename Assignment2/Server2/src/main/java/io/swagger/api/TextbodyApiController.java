package io.swagger.api;

import io.swagger.configuration.RabbitMQSendRequest;
import io.swagger.model.ResultVal;
import io.swagger.model.TextLine;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-05-15T17:56:38.010Z[GMT]")
@RestController
public class TextbodyApiController implements TextbodyApi {

    private static final Logger log = LoggerFactory.getLogger(TextbodyApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    Map<String, Integer> map;

    @Autowired
    RabbitMQSendRequest rabbitMQSendRequest;

    @org.springframework.beans.factory.annotation.Autowired
    public TextbodyApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ResultVal> analyzeNewLine(@Parameter(in = ParameterIn.PATH, description = "the operation to perform on the text", required = true, schema = @Schema()) @PathVariable("function") String function, @Parameter(in = ParameterIn.DEFAULT, description = "text string to analyze", required = true, schema = @Schema()) @Valid @RequestBody TextLine body) {
        String accept = request.getHeader("Content-Type");
        if (accept != null && accept.contains("application/json")) {
            try {
                map = wordCount(body.getMessage());
                rabbitMQSendRequest.send(map);
                return new ResponseEntity<ResultVal>(objectMapper.readValue("{\n  \"message\" :" + map.size() + "\n}", ResultVal.class), HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ResultVal>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ResultVal>(HttpStatus.NOT_IMPLEMENTED);
    }

    private Map<String, Integer> wordCount(String input) {
        Map<String, Integer> map = new HashMap<>();
        for (String s : input.split("\\s+")) {
            map.put(s, map.getOrDefault(s, 0) + 1);
        }
        return map;
    }

}
