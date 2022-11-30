export const menuItems = [{
        id: 1,
        label: 'menuitems.menu.text',
        isTitle: true
    },
    {
        id: 0,
        icon: 'bxs-dashboard',
        label: 'menuitems.main.text',
        link: '/main'
    },
    {
        id: 3,
        label: 'menuitems.manage.container',
        link: '/container',
        icon: 'bx-box',
    },
    {
        id: 2,
        icon: 'bx-briefcase',
        label: 'menuitems.manage.text',
        // link: '/manage'
        subItems: [ {
            id: 5,
            label: 'menuitems.manage.dispatcher',
            link: '/manage/dispatcher',
            parentId: 2
        }, {
            id: 4,
            label: 'menuitems.manage.files',
            link: '/manage/file',
            parentId: 2
        }, {
            id: 6,
            label: 'menuitems.manage.identification',
            link: '/manage/robot-identification',
            parentId: 2
        }]
    },
    {
        id: 7,
        icon: 'bx-user-pin',
        label: 'menuitems.admin.text',
        link: '/admin/user'
    }

];