
import {
    readSzPeoplingno,
    infoOutCollect,
    infoOutCollectCancel,
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
            name: options.name || ''
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        let t = this;
        if (t.data.id) {
            readSzPeoplingno({
                id: t.data.id
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

        } else {
            wx.showToast({
                icon: 'none',
                title: '未找到文章',
            })
        }


    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },
    infoisCollect() {
        const data = {
            infoid: this.data.id,
            outId: 2
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
        let role = wx.getStorageSync('role') || 0;
        if (role != 0) {
            wx.showLoading({
                title: '请稍候',
                mask: true
            })
            const fun = this.data.isCollect ? infoOutCollectCancel : infoOutCollect
            const data = {
                infoid: this.data.id,
                outId: 2
            }
            fun(data).then(res => {
                this.infoisCollect()
            })
        } else {
            wx.showModal({
                title: '提示',
                content: '您当前是游客，该操作需要普通用户登录，是否进行普通用户登录？',
                success(res) {
                    if (res.confirm) {
                        wx.reLaunch({
                            url: '/pages/login/login',
                        })
                    } else if (res.cancel) {
                        console.log('用户点击取消')
                    }
                }
            })
        }
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