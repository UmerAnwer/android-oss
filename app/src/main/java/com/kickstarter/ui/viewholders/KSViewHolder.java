package com.kickstarter.ui.viewholders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kickstarter.libs.LifecycleType;
import com.trello.rxlifecycle.ActivityEvent;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public abstract class KSViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
  LifecycleType {

  protected final View view;
  private final @NonNull PublishSubject<ActivityEvent> lifecycle = PublishSubject.create();

  public KSViewHolder(final @NonNull View view) {
    super(view);
    this.view = view;

    view.setOnClickListener(this);
  }

  /**
   * No-op click implementation. Subclasses should override this method to implement click handling.
   */
  @Override
  public void onClick(final @NonNull View view) {
    Timber.d("Default KSViewHolder onClick event");
  }

  /**
   * Populate a view with data that was bound in `bindData`.
   */
  abstract public void onBind();

  /**
   * Implementations of this should inspect `data` to set instance variables in the view holder that
   * `onBind` can then use without worrying about type safety.
   *
   * @return Return a `boolean` that indicates if this binding happened successfully.
   */
  abstract public void bindData(final @Nullable Object data) throws Exception;

  @NonNull
  public <T> Observable.Transformer<T, T> bindToLifecycle() {
    return source -> source.takeUntil(
      lifecycle.takeFirst(ActivityEvent.DESTROY::equals)
    );
  }

  @Override
  public Observable<ActivityEvent> lifecycle() {
    return lifecycle;
  }

  /**
   * This method is intended to be called only from `KSAdapter` in order for it to inform the view holder
   * of its lifecycle.
   */
  public void lifecycleEvent(final @NonNull ActivityEvent event) {
    lifecycle.onNext(event);
  }
}
