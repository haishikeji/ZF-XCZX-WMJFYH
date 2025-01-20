// theJobPage/pages/searchEmployee/detail.js
import { infoDetail, infoCollect, infoCollectCancel, infoisCollect } from '../../../api/newApi.js'
Page({

    /**
     * 页面的初始数据
     */
    data: {
        id: '',
        detail: {},
        isCollect: false
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.setData({
            id: options.id
        })
        this.infoDetail(options.id)
        this.infoisCollect()
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },
    infoDetail(id) {
        infoDetail({
            id
        }).then(res => {
            let detail = res.data
            detail.timer = this.calculateRelativeTime(res.data.createTime)
            this.setData({
                detail
            })
        })
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
        const role = wx.getStorageSync('role')||0
        if (role == 0) {
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
        } else {
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
        }
    },
    call() {
        wx.makePhoneCall({
            phoneNumber: this.data.detail.phone
        })
    },
    calculateRelativeTime(publicationTime) {
        const currentTime = new Date();
        const publicationDate = new Date(publicationTime);

        // 将时间戳转换为当天的开始时间（午夜）
        const startOfToday = new Date(
            currentTime.getFullYear(),
            currentTime.getMonth(),
            currentTime.getDate()
        );

        // 计算时间差（毫秒）
        const timeDifference = currentTime - publicationDate;
        const oneDay = 24 * 60 * 60 * 1000;

        if (timeDifference < oneDay && publicationDate >= startOfToday) {
            return '今天';
        } else if (timeDifference < oneDay * 2) {
            return '昨天';
        } else if (timeDifference < oneDay * 3) {
            return '前天';
        } else {
            // 返回入参发布时间
            return publicationTime.substring(0, 10);
        }
    },
    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})