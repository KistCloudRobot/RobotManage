
import {http} from "./auth-api"

export class JqlApi {
    constructor(url, schema) {
        this.baseUrl = url;
        this.schema = schema;
        this.columnInfos = schema.columns;
    }

    // @TODO sort, limit, page 처리.
    async list(/*sort, limit, page*/) {
        //let url = this.baseUrl+`/`;
        let res = await http.get(this.baseUrl+`/`);

        return res.data;
    }

    async add(jql) {
        let res = await http.post(this.baseUrl, jql);

        return res.data;
    }

    async findById(id) {
        let res = await http.get(this.baseUrl+`/${id}`);

        return res.data;
    }

    async deleteByIdList(idList) {
        let res = await http.delete(this.baseUrl+`/${idList}`);

        return res.data;
    }

    async updateByIdList(idList, jql) {
        let res = await http.patch(this.baseUrl+`/${idList}`, jql);

        return res.data;
    }

    /**
     *
     * @param jql json object that represents JQL query
     * @param sort array of sort field name with direction sign. ex [ "+name", "-time" ]
     * @param limit size limit for pagination
     * @param page  page number (1 for first page)
     * @returns {Promise<*>}
     */
    async searchByJql(jql, sort, limit, page) {
        let orders = sort || [];
        jql = this.validateJql(jql);

        let res = ''
        if(!page && !limit) {
            res = await http.post(this.baseUrl+`/find`, jql)
        } else {
            res = await http.post(this.baseUrl+`/find?limit=${limit}&page=${page}&sort=${orders}`, jql);
        }
        console.log('searchByJql', res)
        return res.data;
    }

    validateJql(jql) {
        return jql == null ? {} : jql;
    }

    async findTopByJql(jql, sort) {
        let orders = sort || [];
        jql = this.validateJql(jql);
        let res = await http.post(this.baseUrl+`/top?sort=${orders}`, jql);

        return res.data;
    }

    async searchByExample(example, sorts, limit, page) {
        let jql = this.buildJqlByExample(example, sorts)
        return this.searchByJql(jql, sorts, limit, page)
    }

    buildJqlByExample(example, sorts) {
        const sortOptions = this.convertToSortOptions(sorts);
        return this.schema.buildJql("", example, sortOptions);
    }

    convertToSortOptions(sorts) {
        const options = {};
        if (sorts != null) {
            for (let col of sorts) {
                let descent = false;
                switch (col.charAt(0)) {
                    case '-':
                        descent = true;
                    case '+':
                        col = col.substring(1);
                }
                options[col] = descent ? 'desc' : 'asc';
            }
        }
        return options;
    }
}

