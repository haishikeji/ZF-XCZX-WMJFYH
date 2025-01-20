
// pages/home/vote/detail.js
import { getvotedetail, getbgm } from '../../../api/api.js';
const app = getApp()

var _animation; // 动画实体
var _animationIndex = 0; // 动画执行次数index（当前执行了多少次）
var _animationIntervalId = -1; // 动画定时任务id，通过setInterval来达到无限旋转，记录id，用于结束定时任务
const _ANIMATION_TIME = 2000; // 动画播放一次的时长ms


Page({

    /**
     * 页面的初始数据
     */
    data: {
        peoplearr: [],
        tabindex: 1,//1编号2票数
        vid: 0,

        keyword: '',
        page: 1,
        hasmore: true,
        detail: {},
        isShow: false,//授权弹窗
        showpop: false,
        timetip: "",
        timetime: '',
        ismusic: false,
        istap: false,
        animation: '',
        animationstart: 0,
        hasmusic: false
    },

    //授权回调
    onLoadFun: function () {
        this.setData({
            page: 1,
            peoplearr: [],
            hasmore: true
        })
        this.getdetail()
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.setData({
            vid: options.id
        })
        // this.getdetail(1)
        this.getbgmdata()
    },

    getdetail: function (e) {
        wx.showLoading({
            title: '加载中...',
        })
        var that = this
        var dic = {}
        dic.page = that.data.page
        dic.pageSize = 20
        dic.vid = that.data.vid
        var userinfo = wx.getStorageSync('userinfo')
        if (userinfo) {
            dic.openid = userinfo.openid
        } else {
            dic.openid = ''
        }

        dic.key = that.data.keyword
        dic.type = that.data.tabindex
        dic.state = e ? e : 2
        getvotedetail(dic).then(function (res) {
            wx.hideLoading({
                success: (res) => { },
            })
            wx.stopPullDownRefresh({
                success: (res) => { },
            })
            console.log(res)
            if (res.code == 0) {
                that.setData({
                    detail: res.data
                })
                // wx.setNavigationBarTitle({
                //     title: res.data.name,
                // })
                var arr = res.data.szVoteItemPage.records
                if (arr.length < 20) {
                    that.setData({
                        hasmore: false
                    })
                }
                that.setData({
                    peoplearr: that.data.peoplearr.concat(arr)
                })

                if (res.data.timeStatus == 3) {
                    that.setData({
                        showpop: true
                    })
                } else if (res.data.timeStatus == 1) {
                    that.setData({
                        timetip: '开始',
                        timetime: res.data.stime
                    })
                } else if (res.data.timeStatus == 2) {
                    that.setData({
                        timetip: '结束',
                        timetime: res.data.etime
                    })
                }
            } else {
                wx.showToast({
                    title: res.msg,
                    icon: 'none'
                })
            }

        })
    },

    getbgmdata: function () {
        var that = this
        getbgm().then(function (res) {

            if (res.data && res.data.music && res.data.music.length > 0) {
                that.setData({
                    hasmusic: true
                })
                app.globalData.innerAudioContext.src = res.data.music
                app.globalData.innerAudioContext.play()

            } else {
                that.stopAnimationInterval()

            }

        })
    },
    govotedesc: function () {

        wx.navigateTo({
            url: '../vote/votedesc?id=' + this.data.vid,
        })
    },
    tabchange: function (e) {
        var index = e.currentTarget.dataset.index
        this.setData({
            tabindex: index,
            page: 1,
            peoplearr: [],
            hasmore: true
        })
        this.getdetail()
    },

    govote: function (e) {
        if (app.isWxLogin(3)) {
            // var islogin = wx.getStorageSync('islogin')
            // if (islogin == 7 || this.data.isAuthor == 1) {
            wx.navigateTo({
                url: '../vote/votedetail?id=' + this.data.vid + '&vid=' + e.currentTarget.dataset.id,
            })
            // } else {
            //     this.setData({
            //         isShow: true
            //     })
            // }
        }

    },

    searchclick: function () {
        this.setData({
            page: 1,
            peoplearr: [],
            hasmore: true
        })
        this.getdetail()
    },
    getkeyword: function (e) {
        this.setData({
            keyword: e.detail.value
        })
    },
    search: function (e) {
        this.setData({
            keyword: e.detail.value,
            page: 1,
            peoplearr: [],
            hasmore: true
        })
        this.getdetail()
    },

    goback: function () {
        // wx.navigateBack({
        //   delta: 0,
        // })
        this.setData({
            showpop: false
        })

    },

    playmusic: function () {
        if (this.data.ismusic) {
            this.startAnimationInterval()
            app.globalData.innerAudioContext.play()

        } else {
            this.stopAnimationInterval()
            app.globalData.innerAudioContext.pause()

        }
        this.setData({
            ismusic: !this.data.ismusic
        })
    },

    rotateAni: function (n) {
        _animation.rotate(120 * (n)).step()
        this.setData({
            animation: _animation.export()
        })
    },

    /**
     * 开始旋转
     */
    startAnimationInterval: function () {
        var that = this;
        that.rotateAni(++_animationIndex); // 进行一次旋转
        _animationIntervalId = setInterval(function () {
            that.rotateAni(++_animationIndex);
        }, _ANIMATION_TIME); // 每间隔_ANIMATION_TIME进行一次旋转
    },

    /**
     * 停止旋转
     */
    stopAnimationInterval: function () {
        if (_animationIntervalId > 0) {
            clearInterval(_animationIntervalId);
            _animationIntervalId = 0;
        }
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        _animationIndex = 0;
        _animationIntervalId = -1;
        this.data.animation = '';
        this.data.animationstart = 1

        _animation = wx.createAnimation({
            duration: _ANIMATION_TIME,
            timingFunction: 'linear', // "linear","ease","ease-in","ease-in-out","ease-out","step-start","step-end"
            delay: 0,
            transformOrigin: '50% 50% 0'
        })

        this.startAnimationInterval()

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

        app.globalData.innerAudioContext.play()
        if (this.data.animationstart == 1 && this.data.hasmusic) {
            this.startAnimationInterval()
        }

        this.setData({
            page: 1,
            peoplearr: [],
            hasmore: true
        })
        this.getdetail(1)

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

        app.globalData.innerAudioContext.pause()
        this.stopAnimationInterval()
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {
        app.globalData.innerAudioContext.stop()
        this.stopAnimationInterval()
    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {
        this.setData({
            page: 1,
            peoplearr: [],
            hasmore: true
        })
        this.getdetail()
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        if (this.data.hasmore) {
            this.data.page++
            this.getdetail()
        }
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

        return {
            title: '邀请您参加' + '"' + this.data.detail.name + '"' + '投票活动',
            imageUrl: this.data.detail.pic,
            path: '/otherPage/pages/vote/detail?id=' + this.data.vid,
        }
    },
    onShareTimeline: function () {
        return {
            title: '邀请您参加' + '"' + this.data.detail.name + '"' + '投票活动',
            imageUrl: this.data.detail.pic,
            query: 'id=' + this.data.vid,
        }
    },
})