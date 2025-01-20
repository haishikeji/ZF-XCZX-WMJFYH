// pointsPage/pages/duihuanRecords/index.js
import { jifenjilu } from '../../../api/newApi.js';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
        riliShow: false,
        minDate: new Date().getTime() - 365 * 24 * 60 * 60 * 1000,
        maxDate: new Date().getTime(),

        page: 1,
        size: 20,
        pages: 0,
        current: 0,
        total: 0,
        noData: false,
        start: '',
        end: '',
        showFlag: false
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.init();
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
        this.setData({
            showFlag: true
        })
    },

    riliPopShow() {
        this.setData({
            riliShow: true
        })
    },
    onClose() {
        this.setData({
            riliShow: false
        })
    },
    formatDate(date) {
        date = new Date(date);
        let y = date.getFullYear();
        let m = date.getMonth() + 1;
        m < 10 ? (m = '0' + m) : '';
        let d = date.getDate();
        d < 10 ? (d = '0' + d) : '';

        return y + '-' + m + '-' + d;
    },
    onConfirm(event) {
        const [start, end] = event.detail;
        this.setData({
            riliShow: false,
            start: this.formatDate(start),
            end: this.formatDate(end),
        });
        this.init();
    },
    async init() {
        let t = this;
        t.setData({
            page: 1,
            pages: 0,
            current: 0,
            noData: false,
            kong: false
        })
        t.getMore();
    },
    getMore() {
        let t = this;
        var promise = new Promise((resolve, reject) => {
            if (t.data.pages >= t.data.current) {
                let _d = {
                    pageNo: t.data.page,
                    pageSize: t.data.size,
                };

                if (t.data.start && t.data.end) {
                    _d.start = t.data.start + ' 00:00:00';
                    _d.end = t.data.end + ' 23:59:59';
                }
                jifenjilu(_d).then(r => {
                    if (r.httpCode == 200 || r.code == 0) {
                        let d;
                        if (t.data.page == 1) {
                            d = [];
                        } else {
                            d = t.data.d;
                        }
                        if (r.data.records.length) {
                            r.data.records.forEach(i => {
                                i.createTime = i.createTime ? i.createTime.substring(0, 16) : '';
                                d.push(i)
                            })
                            if (r.data.records.length < r.data.size) {
                                t.setData({
                                    noData: true,
                                })
                            } else {
                                t.setData({
                                    noData: false,
                                })
                            }
                            t.setData({
                                kong: false,
                            })
                        } else {
                            t.setData({
                                noData: true,
                            })
                            if (t.data.page == 1) {
                                t.setData({
                                    kong: true,
                                })
                            }
                        }
                        t.setData({
                            list: d,
                            page: ++t.data.page,
                            pages: r.data.pages,
                            current: r.data.current,
                            total: r.data.total
                        })
                    }

                    resolve();
                })
            } else {
                t.setData({
                    noData: true
                })
                resolve();
            }
        });
        return promise;
    },

    daohang(e) {
        if (e.mark.item) {
            let gps = e.mark.item.dingwei.split(',');
            wx.openLocation({
                latitude: Number(gps[1]), // 目标地点的纬度
                longitude: Number(gps[0]), // 目标地点的经度
                name: e.mark.item.shopName,
                address: e.mark.item.address,
                scale: 18, // 地图缩放级别
                success(res) {
                    // 打开成功的回调
                    console.log('打开成功', res);
                },
                fail(err) {
                    // 打开失败的回调
                    console.error('打开失败', err);
                }
            });
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
        if (!this.data.noData) {
            this.getMore();
        }
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})