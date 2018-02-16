package edu.iis.mto.blog.domain;

import edu.iis.mto.blog.domain.errors.DomainError;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.repository.BlogPostRepository;
import edu.iis.mto.blog.domain.repository.LikePostRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.domain.repository.UserRepository;
import edu.iis.mto.blog.mapper.DataMapper;
import edu.iis.mto.blog.services.BlogService;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogManagerTest {

    @MockBean
    UserRepository userRepository;
    
    @MockBean
    LikePostRepository likePostRepository;
    
    @MockBean
	BlogPostRepository blogPostRepository;
    

    @Autowired
    DataMapper dataMapper;

    @Autowired
    BlogService blogService;

    @Test
    public void creatingNewUserShouldSetAccountStatusToNEW() {
        blogService.createUser(new UserRequest("John", "Steward", "john@domain.com"));
        ArgumentCaptor<User> userParam = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userParam.capture());
        User user = userParam.getValue();
        Assert.assertThat(user.getAccountStatus(), Matchers.equalTo(AccountStatus.NEW));
    }

    @Test
    public void confirmedUserCanLikePost() throws Exception {
    	createAll(AccountStatus.CONFIRMED);
    	Assert.assertThat(blogService.addLikeToPost(2L, 1L), Matchers.is(true));
    }
    
    @Test(expected = DomainError.class)
    public void NewUserCannnotLikePost() throws Exception {
    	createAll(AccountStatus.NEW);
    	blogService.addLikeToPost(2L, 1L);
    }
    
    @Test(expected = DomainError.class)
    public void RemovedUserCannnotLikePost() throws Exception {
    	createAll(AccountStatus.REMOVED);
    	blogService.addLikeToPost(2L, 1L);
    }
    
    private void createAll (AccountStatus accountStatus){
    	User user1 = new User();
    	user1.setId(1L);
    	User userConfirmed = new User();
    	userConfirmed.setAccountStatus(accountStatus);
    	userConfirmed.setId(2L);
    	BlogPost blogPost = new BlogPost();
    	blogPost.setUser(user1);
    	when(userRepository.findOne(2L)).thenReturn(userConfirmed);
    	when(blogPostRepository.findOne(1l)).thenReturn(blogPost);
    	when(likePostRepository.findByUserAndPost(userConfirmed, blogPost)).thenReturn(Optional.empty());
    }
    
}
