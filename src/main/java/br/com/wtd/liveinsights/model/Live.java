package br.com.wtd.liveinsights.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lives", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"live_id", "user_id"})
})
public class Live {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    private String liveId;

    @NotNull
    private String title;

    @ManyToOne
    private User user;

    @ManyToOne
    @NotNull
    private Tag tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LiveStatus status = LiveStatus.INATIVO;

    @OneToMany(mappedBy = "live", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CommentsInfo> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CommentsInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentsInfo> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public LiveStatus getStatus() {
        return status;
    }

    public void setStatus(LiveStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}