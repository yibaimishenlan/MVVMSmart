package com.wzq.sample.ui.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wzq.mvvmsmart.base.BaseFragment;
import com.wzq.mvvmsmart.http.DownLoadManager;
import com.wzq.mvvmsmart.http.download.ProgressCallBack;
import com.wzq.mvvmsmart.utils.KLog;
import com.wzq.mvvmsmart.utils.ToastUtils;
import com.wzq.sample.R;
import com.wzq.sample.databinding.FragmentHomeBinding;
import com.wzq.sample.entity.FormEntity;
import com.wzq.sample.ui.tab_bar.activity.TabBarActivity;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import okhttp3.ResponseBody;

/**
 * 截止2019年12月21日累计投入时间:45小时
 * 本项目接口地址:  https://www.oschina.net/action/apiv2/banner?catalog=1
 * 权限申请
 * 多布局
 * 去除黄色警告
 * http拦截器
 * GithubBrowserSample  (NetworkBoundResource), Google AAC 架构中的加载网络or DB的策略
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return com.wzq.sample.BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        binding.setPresenter(new Presenter());
    }

    @Override
    public void initViewObservable() {
        //注册监听相机权限的请求
        viewModel.requestCameraPermissions.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                requestCameraPermissions();
            }
        });

        //注册文件下载的监听
        viewModel.loadUrlEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String url) {
                downFile(url);
            }
        });
    }

    /**
     * 封装布局中的点击事件儿;
     */
    public class Presenter {

        //网络访问点击事件
        public void netWorkClick() {
            NavHostFragment
                    .findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_netWorkFragment);
        }

        //RecycleView多布局
        public void rvMultiClick() {
            NavHostFragment
                    .findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_multiRecycleViewFragment);
        }

        //进入TabBarActivity
        public void startTabBarClick() {
            startActivity(TabBarActivity.class);
        }

        //ViewPager绑定
        public void viewPagerBindingClick() {
            ToastUtils.showShort("点击跳转viewpager");
            NavHostFragment
                    .findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_viewPagerGroupFragment);
        }

        //ViewPager+Fragment
        public void viewPagerGroupBindingClick() {
            NavHostFragment
                    .findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_viewPagerGroupFragment);
        }

        //表单提交点击事件
        public void formSbmClick() {
            NavHostFragment
                    .findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_formFragment);
        }

        //表单修改点击事件
        public void formModifyClick() {
            //模拟一个修改的实体数据
            FormEntity entity = new FormEntity();
            entity.setId("12345678");
            entity.setName("text");
            entity.setSex("1");
            entity.setBir("xxxx年xx月xx日");
            entity.setMarry(true);
            //传入实体数据
            Bundle mBundle = new Bundle();
            mBundle.putParcelable("entity", entity);
            NavHostFragment
                    .findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_formFragment, mBundle);
        }

        //权限申请
        public void permissionsClick() {
            viewModel.requestCameraPermissions.call();
        }

        //全局异常捕获
        public void exceptionClick() {
            //伪造一个异常
            Integer.parseInt("test");
        }

        //文件下载
        public void fileDownLoadClick() {
            viewModel.loadUrlEvent.setValue("http://gdown.baidu.com/data/wisegame/a2cd8828b227b9f9/neihanduanzi_692.apk");

        }
    }

    /**
     * 请求相机权限
     */
    private void requestCameraPermissions() {
        // TODO: wzq 2019/12/17  依赖冲突,暂时注释
        ToastUtils.showShort("请求相机权限");
        //请求打开相机权限
       /* RxPermissions rxPermissions = new RxPermissions(DemoActivity.this);
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            ToastUtils.showShort("相机权限已经打开，直接跳入相机");
                        } else {
                            ToastUtils.showShort("权限被拒绝");
                        }
                    }
                });*/
    }

    private void downFile(String url) {
        String destFileDir = getActivity().getApplication().getCacheDir().getPath();
        KLog.e("destFileDir--" + destFileDir);
        String destFileName = System.currentTimeMillis() + ".apk";
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        /**
         * ProgressCallBack构造方法中，LiveEventBus监听进度改变，调用ProgressCallBack的progress方法设置进度
         */
        DownLoadManager.getInstance().load(url, new ProgressCallBack<ResponseBody>(HomeFragment.this, destFileDir, destFileName) {
            @Override
            public void onStart() {
                super.onStart();
                KLog.e("下载--onStart");
            }

            @Override
            public void onSuccess(ResponseBody responseBody) {
                KLog.e("下载--onSuccess");
                ToastUtils.showShort("文件下载完成！");
            }

            @Override
            public void progress(final long progress, final long total) {
                KLog.e("下载--progress");
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) progress);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtils.showShort("文件下载失败！");
                progressDialog.dismiss();
            }

            @Override
            public void onCompleted() {
                progressDialog.dismiss();
                KLog.e("下载--onCompleted");
            }
        });
    }
}
