package com.example.weather;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


// 这是一个通用超类
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());


        /**
         * 引入Fragment后,activity类被更改为含有FragmentManager类
         * FragmentManager负责管理fragment并将他们的视图添加到activity的视图层
         * 以及管理fragments事务的回退栈和fragment队列
         */
        // 在继承了FragmentActivity以保证兼容性情况下
        // 使用getSupportFragmentManager方法获取FragmentManager对象
        // 若不考虑兼容性可以直接继承Activity类调用getFragmentManager()方法
        FragmentManager fm = getSupportFragmentManager();
        // 从FragmentManager找到对应的fragment,用于添加fragment视图
        // 注意：这里是通过Fragment容器id查找,而不是通过放在容器上面的fragment的资源ID查找
        // 使用容器资源ID去识别UI Fragment是FragmentManager内置的使用机制
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        /**
         * fragment事务：被用来添加、移除、附和、分离或者替换fragment队列中的fragment
         * 是fragment在运行时组装和重新组装用户界面的核心方式
         * beginTransaction()方法
         */
        if(fragment == null){
            fragment = createFragment();
            // 创建并返回FragmentTransaction实例
            // 将创建的fragment添加到fragment容器视图资源上
            // 提交添加fragment的事务
            /***
             *容器视图资源ID告知FragmentManager：fragment视图应该出现在activity视图什么地方
             * 是FragmentManager队列中fragment的唯一标识符
             */
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

    }
}
