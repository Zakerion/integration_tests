package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LikePostRepositoryTest {
    @Autowired
    private LikePostRepository likePostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private BlogPost blogPost;
    private User user1;
    private User user2;

    @Before
    public void setUp() throws Exception {
        user1 = userRepository.findOne(1L);
        user2 = new User();
        user2.setFirstName("Edward");
        user2.setLastName("Nowak");
        user2.setEmail("bzz@domain.com");
        user2.setAccountStatus(AccountStatus.NEW);
        user2 = userRepository.save(user2);
        blogPost = new BlogPost();
        blogPost.setUser(user1);
        blogPost.setEntry("Tresc posta");
        blogPost = blogPostRepository.save(blogPost);
    }

    private LikePost giveLike(User user){
        LikePost likePost = new LikePost();
        likePost.setUser(user);
        likePost.setPost(blogPost);
        return likePostRepository.save(likePost);
    }

    @Test
    public void shouldStoreNewLike() throws Exception {
        LikePost likePost = giveLike(user2);
        Assert.assertThat(likePost.getId(), notNullValue());
    }

    @Test
    public void shouldPostHaveLike() throws Exception {
        LikePost likePost = giveLike(user2);
        testEntityManager.refresh(blogPost);
        assertThat(blogPost.getLikes(), not(empty()));
        assertThat(blogPost.getLikes().get(0).getId(), is(likePost.getId()));
    }

    @Test
    public void shouldPostBeEdited() throws Exception {
        LikePost likePost = giveLike(user2);
        likePost.setUser(user1);
        likePostRepository.save(likePost);
        testEntityManager.refresh(blogPost);
        assertThat(blogPost.getLikes().get(0).getUser().getId(), is(user1.getId()));
    }

    @Test
    public void findByUserAndPost() throws Exception {
        LikePost likePost = giveLike(user2);
        LikePost likePost1 = likePostRepository.findByUserAndPost(user2, blogPost).get();
        assertThat(likePost.getId(), is(likePost1.getId()));
    }

    @Test
    public void findLikeWhichNotExist() throws Exception {
        giveLike(user2);
        boolean foundLike = likePostRepository.findByUserAndPost(user1,blogPost).isPresent();
        assertThat(foundLike, is(false));
    }
}