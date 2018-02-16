package edu.iis.mto.blog.domain;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.iis.mto.blog.api.request.PostRequest;
import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.errors.DomainError;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.services.BlogService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class BlogManager extends DomainService implements BlogService {

    @Override
    public Long createUser(UserRequest userRequest) {
        User user = mapper.mapToEntity(userRequest);
        user.setAccountStatus(AccountStatus.NEW);
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public Long createPost(Long userId, PostRequest postRequest) {
        User user = userRepository.findOne(userId);
        BlogPost post = mapper.mapToEntity(postRequest);
        post.setUser(user);
        blogPostRepository.save(post);
        return post.getId();
    }

    @Override
    public boolean addLikeToPost(Long userId, Long postId) {
        User user = userRepository.findOne(userId);
        BlogPost post = blogPostRepository.findOne(postId);
        if (post.getUser().getId().equals(userId)) {
            throw new DomainError("cannot like own post");
        }
        if(user.getAccountStatus() != AccountStatus.CONFIRMED){
        	throw new DomainError("Only the user with the status of a CONFIRMED can like the post");
        }
        Optional<LikePost> existingLikeForPost = likePostRepository.findByUserAndPost(user, post);
        if (existingLikeForPost.isPresent()) {
            return false;
        }
        LikePost likePost = new LikePost();
        likePost.setUser(user);
        likePost.setPost(post);
        likePostRepository.save(likePost);
        return true;
    }

}
