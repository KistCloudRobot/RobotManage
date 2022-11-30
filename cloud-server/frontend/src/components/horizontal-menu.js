export const menuItems = [{
        id: 1,
        label: "menuitems.dashboard.text",
        icon: "bxs-dashboard",
        link: "/"
    },
    {
        id: 2,
        label: "menuitems.uielements.text",
        icon: "bx-briefcase",
        link: "/ui-components"
    },
    {
        id: 3,
        label: "menuitems.advancedkit.text",
        icon: "bxs-grid",
        subItems: [{
                id: 4,
                label: "menuitems.advancedkit.list.sweetalert",
                link: "/advanced/sweet-alert",
                parentId: 3
            },
            {
                id: 5,
                label: "menuitems.advancedkit.list.rangeslider",
                link: "/advanced/rangeslider",
                parentId: 3
            },
            {
                id: 6,
                label: "menuitems.advancedkit.list.notifications",
                link: "/advanced/notifications",
                parentId: 3
            },
            {
                id: 7,
                label: "menuitems.advancedkit.list.carousel",
                link: "/advanced/carousel",
                parentId: 3
            }
        ]
    },
    {
        id: 8,
        label: "menuitems.apps.text",
        icon: 'bxs-cube-alt',
        subItems: [{
                id: 9,
                label: "menuitems.apps.calendar.text",
                link: '/apps/calendar',
                parentId: 8
            },
            {
                id: 10,
                label: "menuitems.apps.chat.text",
                link: '/apps/chat',
                parentId: 8
            },
            {
                id: 11,
                label: "menuitems.apps.email.text",
                subItems: [{
                        id: 12,
                        label: 'menuitems.apps.email.list.inbox',
                        link: '/email/inbox',
                        parentId: 11
                    },
                    {
                        id: 13,
                        label: 'menuitems.apps.email.list.reademail',
                        link: '/email/read/1',
                        parentId: 11
                    }
                ]
            }
        ]
    },
    {
        id: 14,
        icon: 'bx-layer',
        label: 'menuitems.adminkit.text',
        subItems: [{
                id: 15,
                label: "menuitems.typography.text",
                link: '/typography',
                parentId: 14
            },
            {
                id: 15,
                label: 'menuitems.forms.text',
                subItems: [{
                        id: 16,
                        label: 'menuitems.forms.list.elements',
                        link: '/form/elements',
                        parentId: 15
                    },
                    {
                        id: 17,
                        label: 'menuitems.forms.list.advanced',
                        link: '/form/advanced',
                        parentId: 15
                    }
                ]
            },
            {
                id: 18,
                label: 'menuitems.tables.text',
                subItems: [{
                        id: 19,
                        label: 'menuitems.tables.list.bootstrap',
                        link: '/tables/bootstrap',
                        parentId: 15
                    },
                    {
                        id: 20,
                        label: 'menuitems.tables.list.advanced',
                        link: '/tables/datatable',
                        parentId: 15
                    }
                ]
            },
            {
                id: 21,
                label: 'menuitems.charts.text',
                link: '/charts'
            },
            {
                id: 22,
                label: 'menuitems.icons.text',
                subItems: [{
                        id: 23,
                        label: 'menuitems.icons.list.boxicons',
                        link: '/icons/boxicons'
                    },
                    {
                        id: 24,
                        label: 'menuitems.icons.list.materialdesign',
                        link: '/icons/materialdesign'
                    },
                    {
                        id: 25,
                        label: 'menuitems.icons.list.dripicons',
                        link: '/icons/dripicons'
                    },
                    {
                        id: 26,
                        label: 'menuitems.icons.list.fontawesome',
                        link: '/icons/fontawesome'
                    }
                ]
            },
            {
                id: 27,
                label: 'menuitems.maps.text',
                link: '/maps'
            }
        ]
    },
    {
        id: 28,
        label: 'menuitems.extras.text',
        icon: 'bx-file',
        subItems: [{
                id: 29,
                label: 'menuitems.authentication.text',
                subItems: [{
                        id: 30,
                        label: 'menuitems.authentication.list.login',
                        link: '/auth/login-1'
                    },
                    {
                        id: 31,
                        label: 'menuitems.authentication.list.register',
                        link: '/auth/register-1'
                    },
                    {
                        id: 32,
                        label: 'menuitems.authentication.list.recoverpwd',
                        link: '/auth/recoverpwd-1'
                    },
                    {
                        id: 33,
                        label: 'menuitems.authentication.list.lockscreen',
                        link: '/auth/lock-screen-1'
                    },
                    {
                        id: 34,
                        label: 'menuitems.authentication.list.confirm-mail',
                        link: '/auth/confirm-mail'
                    },
                    {
                        id: 35,
                        label: 'menuitems.authentication.list.verification',
                        link: '/auth/email-verification'
                    },
                    {
                        id: 36,
                        label: 'menuitems.authentication.list.verification-step',
                        link: '/auth/two-step-verification'
                    },
                ]
            },
            {
                id: 37,
                label: 'menuitems.utility.text',
                subItems: [{
                        id: 38,
                        label: 'menuitems.utility.list.starter',
                        link: '/pages/starter'
                    },
                    {
                        id: 39,
                        label: 'menuitems.utility.list.profile',
                        link: '/pages/profile'
                    },
                    {
                        id: 40,
                        label: 'menuitems.utility.list.invoice',
                        link: '/pages/invoice'
                    },
                    {
                        id: 41,
                        label: 'menuitems.utility.list.maintenance',
                        link: '/pages/maintenance'
                    },
                    {
                        id: 42,
                        label: 'menuitems.utility.list.comingsoon',
                        link: '/pages/comingsoon'
                    },
                    {
                        id: 43,
                        label: 'menuitems.utility.list.timeline',
                        link: '/pages/timeline'
                    }, {
                        id: 44,
                        label: 'menuitems.utility.list.pricing',
                        link: '/pages/pricing'
                    }, {
                        id: 45,
                        label: 'menuitems.utility.list.error404',
                        link: '/pages/404'
                    }, {
                        id: 46,
                        label: 'menuitems.utility.list.error500',
                        link: '/pages/500'
                    }
                ]
            }
        ]
    }
];