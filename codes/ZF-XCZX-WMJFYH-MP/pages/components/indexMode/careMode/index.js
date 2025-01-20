// pages/components/careMode/index.js
Component({
    /**
     * 组件的属性列表
     */
    properties: {

    },

    /**
     * 组件的初始数据
     */
    data: {
        list: [{
            text: '获取积分',
            pic: '/images/careMode/07.png',
            url: '/pointsPage/pages/task/index'
        }, {
            text: '积分购物',
            pic: '/images/careMode/06.png',
            url: '/pointsPage/pages/integral/index',
            type: 2  // role==4时建设中
        },
        {
            text: '光荣榜单',
            pic: '/images/careMode/12.png',
            url: '/otherPage/pages/guangrongbang/index',
        },
        // {
        //     text: '居民服务',
        //     pic: '/images/careMode/08.png',
        //     url: '/pages/villagerServe/index',
        //     type: 1
        // }, 
        // {
        //     text: '有事找我',
        //     pic: '/images/careMode/05.png',
        //     url: '/otherPage/pages/leadersPhone/index'
        // }, 
        {
          text: '“三资”公开',
          pic: '/images/careMode/14.png',
          url: '/otherPage/pages/sunVillageAffairs/index/index'
      }, 
        {
            text: '富民超市',
            pic: '/images/careMode/01.png',
            // url: '/otherPage/pages/activity/index?type=3&name=富民超市'
            url: '/otherPage/pages/fuminchaoshi/index'
        }, {
            text: '就业超市',
            pic: '/images/careMode/03.png',
            // url: '/theJobPage/pages/job/index',
            url: 'wx7f5ddf7b91010bef',
            type: 3   //跳转小程序
        }, {
            text: '我要投票',
            pic: '/images/careMode/04.png',
            url: '/otherPage/pages/vote/index'
        },
        {
            text: '监测帮扶',
            pic: '/images/careMode/09.png',
            // url: '/otherPage/pages/activity/index?type=6'
            url: '/otherPage/pages/jiancebangfu/index'
        },
            // {
            //     text: '百姓呼声',
            //     pic: '/images/careMode/02.png',
            //     url: '/otherPage/pages/peopleVoice/demand'
            // },
        ]
    },

    lifetimes: {
        attached() {
            console.log('careMode---attached')
            this.getInfo();
        }
    },
    pageLifetimes: {
        show: function () {
            console.log(2, '-------------------show');
            this.getInfo();
        },
    },

    /**
     * 组件的方法列表
     */
    methods: {
        getInfo() {
            this.setData({
                currentCun: wx.getStorageSync('currentCun'),
                role: wx.getStorageSync('role') || 0
            })
            if (this.data.role == 4) {
                let list = this.data.list.filter(function (item) {
                    return item.url != '/theJobPage/pages/job/index' && item.url != '/volunteer/pages/active/index' && item.url != '/otherPage/pages/leadersPhone/index' && item.url != '/otherPage/pages/jiancebangfu/index' && item.url != '/otherPage/pages/vote/index'&& item.url != '/otherPage/pages/villageAffairs/index';
                });
                list.push({
                    text: '精彩活动',
                    pic: '/images/careMode/13.png',
                    url: '/volunteer/pages/active/index'
                })
                this.setData({
                    list: list
                })
                console.log(list)
            }
        },
        changeCun() {
            let role = wx.getStorageSync('role')
            if (role == 4) {
                wx.navigateTo({
                    url: '/volunteer/pages/setAddress/index',
                })
            } else {
                wx.navigateTo({
                    url: '/pages/login/setAddress',
                })
            }
        },
        changeMode() {
            this.triggerEvent('changeMode', { value: 1 })
        },
        codePopOpen() {
            this.triggerEvent('codePopOpen')
        },
        goPage(e) {
            if (e.mark.item.url) {
                if (e.mark.item.type == 1) {
                    wx.switchTab({
                        url: e.mark.item.url,
                    })
                } else if (e.mark.item.type == 2 && this.data.role == 4) {
                    wx.showToast({
                        title: '正在建设中',
                        icon: 'none'
                    })
                } else if (e.mark.item.type == 3) {
                    wx.navigateToMiniProgram({
                        appId: e.mark.item.url,
                    })
                } else {
                    wx.navigateTo({
                        url: e.mark.item.url,
                    })

                }
            } else {
                wx.showToast({
                    title: '正在建设中',
                    icon: 'none'
                })
            }
        },
        goPoints() {
            wx.navigateTo({
                url: '/pointsPage/pages/task/index',
            })
        },
        tuichu() {
            let role = wx.getStorageSync('role')
            // wx.showModal({
            //     title: '提示',
            //     content: role != 0 ? '确定要退出登录吗？' : '确定要退出游客模式吗？',
            //     complete: (res) => {
            //         if (res.cancel) {
            //         }
            //         if (res.confirm) {
            wx.removeStorageSync('wxUserInfo');
            wx.removeStorageSync('role');
            wx.removeStorageSync('token');
            wx.removeStorageSync('userInfo');
            wx.removeStorageSync('currentCun');
            wx.reLaunch({
                url: '/pages/login/login',
            })
            //         }
            //     }
            // })
        },
    }
})
