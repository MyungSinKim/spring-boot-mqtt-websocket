package jp.pigumer.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Index.class)
public class IndexTest {

    @Inject
    private ObjectMapper mapper;
    
    MockMvc mvc;
    
    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(new Index()).build();
        
    }
    @Test
    public void nowTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(new Now("2016/01/01 00:00:00"))));
    }
}
