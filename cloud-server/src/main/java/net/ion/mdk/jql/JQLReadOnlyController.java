package net.ion.mdk.jql;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import net.ion.mdk.util.ClassUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class JQLReadOnlyController<ENTITY, ID> {

    private final JQLRepository<ENTITY,ID> repository;

    protected JQLReadOnlyController(JQLRepository<ENTITY, ID> service) {
        this.repository = service;
    }

    protected JQLRepository<ENTITY,ID> getRepository() {
        return repository;
    }

    public static HashMap newSimpleMap(String key, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    @Operation(summary = "지정 엔터티 읽기")
    public ENTITY get(@PathVariable("id") ID id) {
        ENTITY entity = repository.find(id);
        if (entity == null) {
            throw new HttpServerErrorException("Entity(" + id + ") is not found", HttpStatus.NOT_FOUND, null, null, null, null);
        }
        return entity;
    }

    @GetMapping(path = "/")
    @ResponseBody
    @Operation(summary = "전체 엔터티 리스트")
    public Object list(@RequestParam(value = "page", required = false) Integer page,
                       @Parameter(name = "limit", example = "10")
                       @RequestParam(value = "limit", required = false) Integer limit,
                       @RequestParam(value = "sort", required = false) String[] _sort) {
        return find(page, limit, _sort, null);
    }

    @PostMapping(path = "/find")
    @ResponseBody
    @Operation(summary = "조건 검색")
    public Object find(@RequestParam(value = "page", required = false) Integer page,
                       @Parameter(name = "limit", example = "10")
                       @RequestParam(value = "limit", required = false) Integer limit,
                       @RequestParam(value = "sort", required = false) String[] _sort,
                       @RequestBody() HashMap<String, Object> filter) {

        Class<ENTITY> type = repository.getEntityType();
        Method regUserMethod = ClassUtils.findPublicMethod(type, "getRegUser");

        if(regUserMethod != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                if(!authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    filter.put("regUser", authentication.getName());
                }
            }
        }

        Sort sort = buildSort(_sort);
        if (page == null) {
            return repository.list(filter, sort, limit == null ? -1 : limit);
        }

        page = page - 1;
        PageRequest pageReq = sort == null ?
                PageRequest.of(page, limit) : PageRequest.of(page, limit, sort);
        return repository.list(filter, pageReq);
    }

    @PostMapping(path = "/top")
    @ResponseBody
    @Operation(summary = "조건 검색 첫 엔터티 읽기")
    public ENTITY top(@RequestParam(value = "sort", required = false) String[] _sort,
                      @RequestBody HashMap<String, Object> filter) {

        Sort sort = buildSort(_sort);
        return repository.findTop(filter, sort);
    }

    public static Sort buildSort(String columns[]) {
        if (columns == null || columns.length == 0) return null;

        ArrayList<Sort.Order> orders = new ArrayList<>();
        for (String col : columns) {
            col = col.trim();
            col = col.replace("_", "__");

            Sort.Order order;
            switch (col.charAt(0)) {
                case '-':
                    col = col.substring(1).trim();
                    order = Sort.Order.desc(col);
                    break;
                case '+':
                    col = col.substring(1).trim();
                    // no-break;
                default:
                    order = Sort.Order.asc(col);
                    break;
            }
            orders.add(order);
        }
        return Sort.by(orders);
    }
}
