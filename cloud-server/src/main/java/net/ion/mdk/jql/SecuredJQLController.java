package net.ion.mdk.jql;

import io.swagger.v3.oas.annotations.Operation;
import net.ion.mdk.auth.repository.UserRepository;
import net.ion.mdk.util.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public abstract class SecuredJQLController<ENTITY extends SecuredEntity, ID> extends JQLController<ENTITY, ID> {


    @Autowired
    UserRepository userRepository;
    private Field authorIdField;

    protected SecuredJQLController(JQLRepository<ENTITY, ID> service) {
        super(service);
    }

    /**
     * AuthorId 필드의 접근 권한 부여
     */
    @PostConstruct
    void onCreate() {
        Class<ENTITY> type = super.getRepository().getEntityType();
        this.authorIdField = ClassUtils.findAnnotatedField(type, SecuredEntity.AuthorID.class);
        if (authorIdField == null) {
            throw new RuntimeException("SecuredEntity must have annotated field with SecuredEntity.@AuthorId");
        }
        authorIdField.setAccessible(true);
    }

    protected String getAuthorIdFieldName() {
        return authorIdField.getName();
    }

    protected Object getAuthorId(ENTITY entity) throws IllegalAccessException {
        return authorIdField.get(entity);
    }

    protected void setAuthorId(ENTITY entity, Object authorId) throws IllegalAccessException {
        this.authorIdField.set(entity, authorId);
    }

    @PostMapping()
    @ResponseBody
    @Operation(summary = "엔터티 추가")
    // MDK-OAuth. JWT 토큰을 이용한 사용자 권한 검사 (OAuth profile 사용 시에만 작동)
    @PreAuthorize("hasRole('USER')")
    public ENTITY add(@RequestBody ENTITY entity) throws Exception {
        Object author = getAuthorId(entity);
        if (author == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            setAuthorId(entity, authentication.getName());
        }
        else {
            ensureCurrentAccount(author);
        }
        entity = getRepository().insert(entity);
        return entity;
    }

    private void ensureCurrentAccount(Object authorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authorId.equals(authentication.getName())) {
            throw new SecurityException("AuthorId mismatched with current account");
        }
    }

    @PatchMapping(path = "/{idList}")
    @ResponseBody
    @Operation(summary = "엔터티 변경")
    public List<ENTITY> update(@PathVariable("idList") ID[] idList, @RequestBody HashMap<String, Object> updateSet) throws Exception {
        List<ENTITY> entities;
        Object author = updateSet.remove(getAuthorIdFieldName());
        if (author != null) {
            ensureCurrentAccount(author);
        }
        ensureEditorAuthority(idList);
        entities = getRepository().update(idList, updateSet);
        return entities;
    }

    @DeleteMapping("/{idList}")
    @ResponseBody
    @Operation(summary = "엔터티 삭제")
    public ID[] delete(@PathVariable("idList") ID[] idList) {
        ensureEditorAuthority(idList);
        getRepository().delete(idList);
        return idList;
    }

    protected void ensureEditorAuthority(ID[] idList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().contains("ROLE_ADMIN")) {
            HashMap filter = newSimpleMap(getRepository().getPrimaryKey(), idList);
            filter.put(getAuthorIdFieldName(), authentication.getName());
            long count = getRepository().count(filter);
            if (count != idList.length) {
                throw new SecurityException("Can not update not owned by current account");
            }
        }
    }
}

