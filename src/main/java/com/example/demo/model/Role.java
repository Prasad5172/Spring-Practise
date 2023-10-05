package com.example.demo.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;
 

}
 // if you need to fetch users based on role then you need to mention a bidirectional mapping like mappedBy attribute 
//  chat gpt
// In your current setup, you have defined a unidirectional many-to-many relationship from the `User` entity to the `Role` entity. This means that a `User` can have multiple roles, but you haven't established a direct relationship from the `Role` entity to the `User` entity.

// Whether you should establish a bidirectional relationship (i.e., having a relationship from `Role` to `User`) depends on your application's requirements and how you intend to use the data.

// Here are some considerations:

// 1. **User-to-Role Relationship:** The unidirectional relationship from `User` to `Role` allows you to efficiently fetch roles for a given user. You can easily retrieve the roles associated with a user. If this is all you need, you can keep the relationship as it is.

// 2. **Role-to-User Relationship:** If you also need to fetch users associated with a specific role, you would need a bidirectional relationship. In this case, you would need to define a similar many-to-many relationship in the `Role` entity.

//    Example in the `Role` entity:

//    ```java
//    @ManyToMany(mappedBy = "roles")
//    private Set<User> users = new HashSet<>();
//    ```

//    This would allow you to retrieve users associated with a specific role.

// 3. **Performance Considerations:** Be mindful of the potential performance implications of bidirectional relationships. Fetching all users for a role or all roles for a user could result in large datasets. Consider using lazy loading or pagination to manage the data retrieval efficiently.

// Ultimately, the decision to establish a bidirectional relationship depends on your application's use cases. If you need to frequently navigate from `Role` to `User` and vice versa, consider defining the bidirectional relationship. However, if you primarily need to fetch roles for users, the unidirectional relationship you currently have is sufficient.
