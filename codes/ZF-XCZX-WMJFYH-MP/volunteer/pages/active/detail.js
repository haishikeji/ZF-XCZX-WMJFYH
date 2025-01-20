// volunteer/pages/active/detail.js
import {
    amazingActiveDetail,
} from '../../../api/volunteer.js';
import WxParse from '../../../wxParse/wxParse';
Page({
    /**
     * 页面的初始数据
     */
    data: {

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log(options)
        this.setData({
            id: options.id,
        })

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        let t = this;

        amazingActiveDetail(t.data.id).then(r => {
            if (r.code == 0) {
                WxParse.wxParse('rule', 'html', r.data.content, t, 0);
                r.data.createTime = r.data.createTime.substring(0, 10);
                t.setData({
                    d: r.data,
                    name: r.data.title
                })
            }
        })


        this.videoContext = wx.createVideoContext('myvideo')
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {
        this.videoContext.pause()
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {
        this.videoContext.stop()
    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击分享
     */
    onShareAppMessage: function () {
        let t = this;
        return {
            title: t.data.d.title, // 转发后 所显示的title
            path: '/otherPage/pages/activity/detail?index=' + t.data.index + '&id=' + t.data.id + '&name=' + t.data.name, // 相对的路径
        }
    }

})