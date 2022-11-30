import { auth_checkUserAuthority } from './auth-api.js'

export class JQLField {
    constructor(name, label, validators) {
        this.key = name; // b-table에서도 사용 가능
        this.field = name; // vue-good-table 용.
        this.label = label;
        this.jqlOp = "@eq"
        this.sortable = true
        this.editableAuthorities = null;
        if (!validators) {
            this.validators = [];
            this.required = false;
        }
        else {
            if (!Array.isArray(validators)) {
                validators = [validators];
            }
            this.validators = validators;
            this.required = this.validate(null) != null;
        }
    }

    validate(value, valueSet) {
        for (const validator of this.validators) {
            const err = validator(value, valueSet);
            if (err != null) return err;
        }
        return null;
    }

    setFormatter(formatter) {
        this.formatter = formatter;
        return this;
    }

    allowUpdate(editableAuthorities) {
        this.editableAuthorities = editableAuthorities;
        return this;
    }

    setSortable(sortable) {
        this.sortable = sortable;
        return this;
    }
}

export class TextColumn extends JQLField {
    constructor(name, label, validators) {
        super(name, label, validators)
        this.type = "text";
        this.jqlOp = "@like"
    }
}

export class NumberColumn extends JQLField {
    constructor(name, label, validators) {
        super(name, label, validators)
        this.type = "number";
        this.jqlOp = "@ge_lt"
    }
}

function dateFormat(date) {
    let month = date.getMonth() + 1;
    let day = date.getDate();
    let hour = date.getHours();
    let minute = date.getMinutes();
    let second = date.getSeconds();

    month = month >= 10 ? month : '0' + month;
    day = day >= 10 ? day : '0' + day;
    hour = hour >= 10 ? hour : '0' + hour;
    minute = minute >= 10 ? minute : '0' + minute;
    second = second >= 10 ? second : '0' + second;

    return date.getFullYear() + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
}

function dateRangeFilter(filterString) {
    if (filterString == null) {
        return null;
    }
    let dateRange = filterString.split(" to ");
    if (dateRange.length > 1) {
        console.log("----- datetime ", dateRange[0].length, " ", dateRange[1].length)
        if (dateRange[0].length <= 10) {
            dateRange[0] += ' 00:00:00'
        }
        if (dateRange[1].length <= 10) {
            const date = new Date(dateRange[1]);
            const next_date = new Date(date.getFullYear(), date.getMonth(), date.getDate() + 1)
            dateRange[1] = dateFormat(next_date)
        }
    } else {
        const date = new Date(dateRange[0]);
        const next_date = new Date(date.getFullYear(), date.getMonth(), date.getDate() + 1)
        dateRange[0] = dateRange + ' 00:00:00';
        dateRange[1] = dateFormat(next_date)
    }
    console.log("----- date ", dateRange)
    return dateRange;
}

export class DateColumn extends NumberColumn {
    constructor(name, label, datePickerConfig, validators) {
        super(name, label, validators)
        this.type = "datetime";
        this.dateInputFormat = "yyyy-MM-dd";
        this.dateOutputFormat = "yyyy-MM-dd";
        this.datePickerConfig = datePickerConfig? datePickerConfig :{
            dateFormat: "Y-m-d",
            mode: "range",
            allowInput: true
        }
    }

    columnType(type) {
        this.type = type;
        return this;
    }

    // dateRangeFilter(filterString) {
    //     // const vm = this;
    //     if (filterString == null) {
    //         return null;
    //     }
    //     let dateRange = filterString.split(" to ");
    //     if (dateRange.length > 1) {
    //         console.log("----- datetime ", dateRange[0].length, " ", dateRange[1].length)
    //         if (dateRange[0].length <= 10) {
    //             dateRange[0] += ' 00:00:00'
    //         }
    //         if (dateRange[1].length <= 10) {
    //             const date = new Date(dateRange[1]);
    //             const next_date = new Date(date.getFullYear(), date.getMonth(), date.getDate() + 1)
    //             dateRange[1] = dateFormat(next_date)
    //         }
    //     }
    //     console.log("----- date ", dateRange)
    //     return dateRange;
    // }
}

export class Dropdown extends JQLField {
    constructor(name, label, dropdownItems, validators) {
        super(name, label, validators)
        this.dropdownOptions = dropdownItems;
        this.formatter = this.formatterFn.bind(this);
        this.sortByFormatted = this.formatter;
        if (Array.isArray(dropdownItems)) {
            this.type = 'dropdown-array'
        }
        else {
            this.type = 'dropdown-objects'
        }
        return this;
    }

    formatterFn(value, key=null, item=null) {
        const items = this.dropdownOptions
        // console.log('formatterFn', key, value, items[value])
        return items[value];
    }
}

export class Checkbox extends JQLField {
    constructor(name, label, dropdownItems) {
        super(name, label, true)
        this.dropdownOptions = dropdownItems;
        return this;
    }
}

/*
class JoinProxy {
    constructor(baseName, schema) {
        //const cache = {}
        const proxy = new Proxy(schema, {
            cache: {},
            get: function (target, attr){
                let column = this.cache[attr];
                if (column === undefined) {
                    if (attr in target) {
                        column = target[attr];
                    }
                    else {
                        return undefined;
                    }
                    column = new Proxy(column, {
                        get: function (target, field) {
                            let v = target[field];
                            if (field == 'field' || field == 'key') {
                                v = baseName + v;
                            }
                            console.log("join proxy", field, v)
                            return v;
                        }
                    })
                    this.cache[attr] = column;
                }
                return column;
            }
        })
        return proxy;
    }
}
*/


export class JqlSchema {
    constructor(columnInfos) {
        for (const col of columnInfos) {
            this[col.key] = col;
        }
        this.columns = columnInfos
    }

    getEditableColumns(userType) {
        let editableColumns = this.columns.filter(col => auth_checkUserAuthority(col.editableAuthorities))
        return editableColumns;
    }

    join(fk, schema) {
        const subColumns = []
        for (const col of schema.columns) {
            const subColumn = Object.assign({}, col)
            subColumns.push(subColumn)
        }
        const joinedSchema = new JqlSchema(subColumns);
        for (const col of joinedSchema.columns) {
            col.field = col.key = fk + '.' + col.key;
        }
        this[fk] = joinedSchema;
        return joinedSchema;
    }

    buildJql(prefix, example, sorts) {
        if (example == null) {
            return;
        }
        const jql = {};
        for (let key in example) {
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length)
            }
            let value = example[key];
            if (value == null) continue;

            let column = this[key];
            if (column === undefined) {
                throw "Error!! Unknown jql Field: " + key;
            }
            if (column instanceof JqlSchema) {
                jql[key] = column.buildJql(key + ".", value, sorts);
                continue;
            }
            let op = column.jqlOp;
            console.log(column, op)
            const enable_auto_range_filter_with_sort_direction = true;
            switch (op) {
                case '@like':
                    value = '%' + value + '%'
                    console.log("like" + value)
                    break;
                case '@ge_lt':
                case '@gt_lt':
                case '@ge_le':
                case '@gt_le':
                    if (enable_auto_range_filter_with_sort_direction && !Array.isArray(value)) {
                        let order = sorts[key];
                        if (order == null) {
                            order = column.sortOrder;
                        }

                        const single_date_has_a_range = true;
                        if (single_date_has_a_range && column.type == 'datetime') {
                            value = dateRangeFilter(value);
                        }
                        else if (order == 'asc') {
                            op = "@ge"; // op.substring(0, 3)
                            if (column.type == 'datetime' && value.length <= 10) {
                                value += ' 00:00:00'
                            }
                        }
                        else {
                            if (column.type == 'datetime' && value.length <= 10) {
                                const date = new Date(value);
                                const next_date = new Date(date.getFullYear(), date.getMonth(), date.getDate() + 1)
                                value = dateFormat(next_date)
                                op = "@lt"
                            }
                            else {
                                op = '@le';// + op.substring(4)
                            }
                        }
                    }
                    break;
                case '@eq':
                    if (!value && column.dropdownOptions != null) {
                        op = "@isNull";
                        value = false;
                    }
                    break;
                default:
                    break;
            }
            jql[key + op] = value
        }
        return jql;
    }

    static getValue(entity, key, initialize = false) {
        if (key == null) {
            console.log('invalid key')
            return;
        }
        let path = key.split('.');
        let v = entity;
        for (let i = 0; i < path.length; i ++) {
            let name = path[i];
            let org_v = v[name];
            if (initialize) {
                if (org_v === undefined) {
                    org_v = (i < path.length - 1) ? {} : null;
                    v[name] = org_v;
                } else if (org_v == null && i < path.length - 1) {
                    org_v = {}
                    v[name] = org_v;
                }
            } else if (org_v == null) {
                return null;
            }
            v = org_v;
        }
        return v;
    }


    static toPlainValueSet(entity) {
        const values = {};
        function toPlainValueSet_internal(obj, prefix) {
            for (let key in obj) {
                const v = obj[key];
                if (typeof v == "object") {
                    toPlainValueSet_internal(v, prefix + key + '.');
                }
                else {
                    values[prefix + key] = v;
                }
            }
        }
        toPlainValueSet_internal(entity, "");
        console.log('toPlainValueSet_internal', values)
        return values;
    }

    static encodePlainValueSet(valueSet) {
        const entity = {}
        for (const p in valueSet) {
            const v = valueSet[p];
            if (v) {
                JqlSchema.setValue(entity, p, v);
            }
        }
        return entity;
    }

    static setValue(entity, key, value) {
        if (key == null) {
            console.log('invalid key')
            return;
        }
        let path = key.split('.');
        let i = 0;
        for (; i < path.length - 1; i ++) {
            let v = entity[path[i]];
            if (v == null) {
                entity[path[i]] = v = {}
            }
            entity = v;
        }
        entity[path[i]] = value;
    }

    static filterEntitiesByExample(items, fields, searchParams) {
        const filters = []
        for (const col of fields) {
            const v = searchParams[col.key]
            console.log('filterEntitiesByExample-0', col.key, v)
            const isDropdown = col.dropdownOptions != null
            if (v && (!isDropdown || v != -1)) {
                let filter = {
                    key: col.key,
                    value: v,
                    exactMatch: isDropdown
                }
                filters.push(filter)
            }
        }

        if (filters.length == 0) {
            return items;
        }

        const rows = items.filter(row => {
            for (const filter of filters) {
                const v = JqlSchema.getValue(row, filter.key)
                console.log('filterEntitiesByExample', filter.key, v)
                if (filter.exactMatch) {
                    if (v != filter.value) return false;
                }
                else if (v.indexOf(filter.value) < 0) {
                    return false;
                }
            }
            return true;
        });
        return rows;
    }
}


