// pages/home/peopledetail.js
// import {
//   orgByCateDetail,
//   noticesDetail,
//   farmsdetail
// } from '../../api/api.js';
import {
    infoDetail,
    infoCollect,
    infoCollectCancel,
    infoisCollect
} from '../../../api/newApi.js';
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
            index: options.index,
            name: options.name + '详情',
            type: options.type
        })
        // if (options.index == 3 || options.index == 13) {
        //     wx.setNavigationBarTitle({
        //         title: options.name + '详情',
        //     })
        // } else if (options.index == 4) {
        //     wx.setNavigationBarTitle({
        //         title: options.name + '详情',
        //     })
        // } else if (options.index == 5) {
        //     wx.setNavigationBarTitle({
        //         title: '优秀村户',
        //     })
        // } else if (options.index == 6 || options.index == 14) {
        //     wx.setNavigationBarTitle({
        //         title: '种植养殖政策详情',
        //     })
        // }
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        let t = this;
        // let func;
        // if (t.data.index == '13') {
        //     func = noticesDetail;

        // } else if (t.data.index == '5' || t.data.index == '6' || t.data.index == '14') {
        //     func = farmsdetail
        // }

        // else {

        //     func = orgByCateDetail;
        // }
        infoDetail({
            id: t.data.id,
            d: ''
        }).then(r => {
            if (r.code == 0) {
                WxParse.wxParse('rule', 'html', r.data.content, t, 0);
                r.data.createTime = r.data.createTime.substring(0, 10);
                t.setData({
                    d: r.data,
                    name: r.data.title
                })
            }
        })
        let role = wx.getStorageSync('role')
        if (role != 0) {
            this.infoisCollect()
        }


        this.videoContext = wx.createVideoContext('myvideo')
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },
    infoisCollect() {
        const data = {
            infoid: this.data.id,
        }
        infoisCollect(data).then(res => {
            this.setData({
                isCollect: res.data
            })
            setTimeout(() => {
                wx.hideLoading();
            }, 500)
        })
    },
    collect() {
        wx.showLoading({
            title: '请稍候',
            mask: true
        })
        const fun = this.data.isCollect ? infoCollectCancel : infoCollect
        const data = {
            infoid: this.data.id,
        }
        fun(data).then(res => {
            this.infoisCollect()
        })
    },
    callPhone(e) {
        wx.makePhoneCall({
            phoneNumber: e.mark.phone,
            success(res) {
                console.log('拨打电话成功', res);
            },
            fail(err) {
                console.error('拨打电话失败', err);
            }
        });
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