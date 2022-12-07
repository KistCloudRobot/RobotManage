package net.ion.mdk.jql;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

public abstract class JQLController<ENTITY, ID> extends JQLReadOnlyController<ENTITY, ID> {

    protected JQLController(JQLRepository<ENTITY, ID> service) {
        super(service);
    }

    /**
     * 엔터티 추가
     * @param entity
     * @return
     * @throws Exception
     */
    @PostMapping()
    @ResponseBody
    @Operation(summary = "엔터티 추가")
    public ENTITY add(@RequestBody ENTITY entity) throws Exception {
        entity = getRepository().insert(entity);
        return entity;
    }

    /**
     * 엔터티 변경
     * @param idList
     * @param updateSet
     * @return
     * @throws Exception
     */
    @PatchMapping(path = "/{idList}")
    @ResponseBody
    @Operation(summary = "엔터티 변경")
    public List<ENTITY> update(@PathVariable("idList") ID[] idList, @RequestBody HashMap<String, Object> updateSet) throws Exception {
        List<ENTITY> entities = getRepository().update(idList, updateSet);
        return entities;
    }

    /**
     * 엔터티 삭제
     * @param idList
     * @return
     */
    @DeleteMapping("/{idList}")
    @ResponseBody
    @Operation(summary = "엔터티 삭제")
    public ID[] delete(@PathVariable("idList") ID[] idList) {
        getRepository().delete(idList);
        return idList;
    }

}
